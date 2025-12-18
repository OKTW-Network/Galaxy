/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.util.thread.PriorityConsecutiveExecutor;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.IOWorker.Priority;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import one.oktw.galaxy.util.KotlinCoroutineTaskExecutor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(IOWorker.class)
public abstract class MixinAsyncChunk_IOWorker {
    @Unique
    private final AtomicBoolean writeLock = new AtomicBoolean(false);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Mutable
    @Shadow
    @Final
    private PriorityConsecutiveExecutor consecutiveExecutor;

    @Mutable
    @Shadow
    @Final
    private SequencedMap<ChunkPos, IOWorker.PendingStore> pendingWrites;

    @Shadow
    protected abstract void runStore(ChunkPos pos, IOWorker.PendingStore result);

    @Shadow
    public abstract CompletableFuture<Void> scanChunk(ChunkPos pos, StreamTagVisitor scanner);

    @Shadow
    protected abstract boolean isOldChunk(CompoundTag nbt);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void parallelExecutor(RegionStorageInfo storageKey, Path directory, boolean dsync, CallbackInfo ci) {
        pendingWrites = new ConcurrentSkipListMap<>(Comparator.comparingLong(ChunkPos::toLong));
        consecutiveExecutor = new KotlinCoroutineTaskExecutor(3 /* FOREGROUND,BACKGROUND,SHUTDOWN */, "IOWorker-" + storageKey.type());
    }

    /**
     * @author James58899
     * @reason no low priority write & bulk write
     */
    @Overwrite
    private void storePendingChunk() {
        if (!this.pendingWrites.isEmpty() && !writeLock.getAndSet(true)) {
            HashMap<Long, ArrayList<Tuple<ChunkPos, IOWorker.PendingStore>>> map = new HashMap<>();
            pendingWrites.forEach((pos, result) -> map.computeIfAbsent(ChunkPos.asLong(pos.getRegionX(), pos.getRegionZ()), k -> new ArrayList<>()).add(new Tuple<>(pos, result)));
            map.values().forEach(list ->
                consecutiveExecutor.schedule(new StrictQueue.RunnableWithPriority(Priority.FOREGROUND.ordinal(), () -> list.forEach(pair -> runStore(pair.getA(), pair.getB()))))
            );
            this.consecutiveExecutor.schedule(new StrictQueue.RunnableWithPriority(Priority.BACKGROUND.ordinal(), () -> {
                writeLock.set(false);
                storePendingChunk();
            }));
        }
    }

    /**
     * @author James58899
     * @reason no low priority write
     */
    @Overwrite
    private void tellStorePending() {
        storePendingChunk();
    }

    @Inject(method = "loadAsync", at = @At("HEAD"), cancellable = true)
    private void fastRead(ChunkPos pos, CallbackInfoReturnable<CompletableFuture<Optional<CompoundTag>>> cir) {
        IOWorker.PendingStore result = this.pendingWrites.get(pos);
        if (result != null) {
            cir.setReturnValue(CompletableFuture.completedFuture(Optional.ofNullable(result.copyData())));
        }
    }

    @Inject(method = "scanChunk", at = @At("HEAD"), cancellable = true)
    private void fastScan(ChunkPos pos, StreamTagVisitor scanner, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        IOWorker.PendingStore result = this.pendingWrites.get(pos);
        if (result != null) {
            if (result.data != null) result.data.acceptAsRoot(scanner);
            cir.setReturnValue(CompletableFuture.completedFuture(null));
        }
    }

    @Inject(method = "runStore", at = @At(value = "HEAD"), cancellable = true)
    private void removeResults(ChunkPos pos, IOWorker.PendingStore result, CallbackInfo ci) {
        if (!this.pendingWrites.remove(pos, result)) { // Only write once
            ci.cancel();
        }
    }

    /**
     * @author James58899
     * @reason Parallelization
     */
    @Overwrite
    private CompletableFuture<BitSet> createOldDataForRegion(int chunkX, int chunkZ) {
        return CompletableFuture.supplyAsync(
            () -> {
                BitSet bitSet = new BitSet();
                ChunkPos.rangeClosed(ChunkPos.minFromRegion(chunkX, chunkZ), ChunkPos.maxFromRegion(chunkX, chunkZ))
                    .parallel()
                    .forEach(
                        chunkPos -> {
                            CollectFields selectiveNbtCollector = new CollectFields(
                                new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data")
                            );

                            try {
                                scanChunk(chunkPos, selectiveNbtCollector).join();
                            } catch (Exception var7) {
                                LOGGER.warn("Failed to scan chunk {}", chunkPos, var7);
                                return;
                            }

                            if (selectiveNbtCollector.getResult() instanceof CompoundTag nbtCompound && isOldChunk(nbtCompound)) {
                                int ix = chunkPos.getRegionLocalZ() * 32 + chunkPos.getRegionLocalX();
                                synchronized (bitSet) {
                                    bitSet.set(ix);
                                }
                            }
                        }
                    );
                return bitSet;
            },
            Util.backgroundExecutor()
        );
    }
}
