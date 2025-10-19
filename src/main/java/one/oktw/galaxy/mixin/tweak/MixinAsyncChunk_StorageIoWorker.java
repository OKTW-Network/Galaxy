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

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.PrioritizedConsecutiveExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.StorageIoWorker.Priority;
import net.minecraft.world.storage.StorageKey;
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

@Mixin(StorageIoWorker.class)
public abstract class MixinAsyncChunk_StorageIoWorker {
    @Unique
    private final AtomicBoolean writeLock = new AtomicBoolean(false);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Mutable
    @Shadow
    @Final
    private PrioritizedConsecutiveExecutor executor;

    @Mutable
    @Shadow
    @Final
    private SequencedMap<ChunkPos, StorageIoWorker.Result> results;

    @Shadow
    protected abstract void write(ChunkPos pos, StorageIoWorker.Result result);

    @Shadow
    public abstract CompletableFuture<Void> scanChunk(ChunkPos pos, NbtScanner scanner);

    @Shadow
    protected abstract boolean needsBlending(NbtCompound nbt);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void parallelExecutor(StorageKey storageKey, Path directory, boolean dsync, CallbackInfo ci) {
        results = new ConcurrentSkipListMap<>(Comparator.comparingLong(ChunkPos::toLong));
        executor = new KotlinCoroutineTaskExecutor(3 /* FOREGROUND,BACKGROUND,SHUTDOWN */, "IOWorker-" + storageKey.type());
    }

    /**
     * @author James58899
     * @reason no low priority write & bulk write
     */
    @Overwrite
    private void writeResult() {
        if (!this.results.isEmpty() && !writeLock.getAndSet(true)) {
            HashMap<Long, ArrayList<Pair<ChunkPos, StorageIoWorker.Result>>> map = new HashMap<>();
            results.forEach((pos, result) -> map.computeIfAbsent(ChunkPos.toLong(pos.getRegionX(), pos.getRegionZ()), k -> new ArrayList<>()).add(new Pair<>(pos, result)));
            map.values().forEach(list ->
                executor.send(new TaskQueue.PrioritizedTask(Priority.FOREGROUND.ordinal(), () -> list.forEach(pair -> write(pair.getLeft(), pair.getRight()))))
            );
            this.executor.send(new TaskQueue.PrioritizedTask(Priority.BACKGROUND.ordinal(), () -> {
                writeLock.set(false);
                writeResult();
            }));
        }
    }

    /**
     * @author James58899
     * @reason no low priority write
     */
    @Overwrite
    private void writeRemainingResults() {
        writeResult();
    }

    @Inject(method = "readChunkData", at = @At("HEAD"), cancellable = true)
    private void fastRead(ChunkPos pos, CallbackInfoReturnable<CompletableFuture<Optional<NbtCompound>>> cir) {
        StorageIoWorker.Result result = this.results.get(pos);
        if (result != null) {
            cir.setReturnValue(CompletableFuture.completedFuture(Optional.ofNullable(result.copyNbt())));
        }
    }

    @Inject(method = "scanChunk", at = @At("HEAD"), cancellable = true)
    private void fastScan(ChunkPos pos, NbtScanner scanner, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        StorageIoWorker.Result result = this.results.get(pos);
        if (result != null) {
            if (result.nbt != null) result.nbt.accept(scanner);
            cir.setReturnValue(CompletableFuture.completedFuture(null));
        }
    }

    @Inject(method = "write", at = @At(value = "HEAD"), cancellable = true)
    private void removeResults(ChunkPos pos, StorageIoWorker.Result result, CallbackInfo ci) {
        if (!this.results.remove(pos, result)) { // Only write once
            ci.cancel();
        }
    }

    /**
     * @author James58899
     * @reason Parallelization
     */
    @Overwrite
    private CompletableFuture<BitSet> computeBlendingStatus(int chunkX, int chunkZ) {
        return CompletableFuture.supplyAsync(
            () -> {
                BitSet bitSet = new BitSet();
                ChunkPos.stream(ChunkPos.fromRegion(chunkX, chunkZ), ChunkPos.fromRegionCenter(chunkX, chunkZ))
                    .parallel()
                    .forEach(
                        chunkPos -> {
                            SelectiveNbtCollector selectiveNbtCollector = new SelectiveNbtCollector(
                                new NbtScanQuery(NbtInt.TYPE, "DataVersion"), new NbtScanQuery(NbtCompound.TYPE, "blending_data")
                            );

                            try {
                                scanChunk(chunkPos, selectiveNbtCollector).join();
                            } catch (Exception var7) {
                                LOGGER.warn("Failed to scan chunk {}", chunkPos, var7);
                                return;
                            }

                            if (selectiveNbtCollector.getRoot() instanceof NbtCompound nbtCompound && needsBlending(nbtCompound)) {
                                int ix = chunkPos.getRegionRelativeZ() * 32 + chunkPos.getRegionRelativeX();
                                synchronized (bitSet) {
                                    bitSet.set(ix);
                                }
                            }
                        }
                    );
                return bitSet;
            },
            Util.getMainWorkerExecutor()
        );
    }
}
