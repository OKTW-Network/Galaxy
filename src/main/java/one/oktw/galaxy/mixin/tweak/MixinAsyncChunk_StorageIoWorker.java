/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.StorageIoWorker.Priority;
import net.minecraft.world.storage.StorageKey;
import one.oktw.galaxy.util.KotlinCoroutineTaskExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(StorageIoWorker.class)
public abstract class MixinAsyncChunk_StorageIoWorker {
    private final AtomicBoolean writeLock = new AtomicBoolean(false);

    @Mutable
    @Shadow
    @Final
    private TaskExecutor<TaskQueue.PrioritizedTask> executor;

    @Mutable
    @Shadow
    @Final
    private Map<ChunkPos, StorageIoWorker.Result> results;

    @Shadow
    protected abstract void write(ChunkPos pos, StorageIoWorker.Result result);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void parallelExecutor(StorageKey storageKey, Path directory, boolean dsync, CallbackInfo ci) {
        results = new ConcurrentHashMap<>();
        executor = new KotlinCoroutineTaskExecutor<>(new TaskQueue.Prioritized(4 /* FOREGROUND,BACKGROUND,WRITE_DONE,SHUTDOWN */), "IOWorker-" + storageKey.type());
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

    /**
     * @author James58899
     * @reason no delay set result
     */
    @Overwrite
    public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable NbtCompound nbt) {
        StorageIoWorker.Result result = this.results.computeIfAbsent(pos, pos2 -> new StorageIoWorker.Result(nbt));
        result.nbt = nbt;
        return result.future;
    }

    @Inject(method = "readChunkData", at = @At("HEAD"), cancellable = true)
    private void fastRead(ChunkPos pos, CallbackInfoReturnable<CompletableFuture<Optional<NbtCompound>>> cir) {
        StorageIoWorker.Result result = this.results.get(pos);
        if (result != null) {
            cir.setReturnValue(CompletableFuture.completedFuture(Optional.ofNullable(result.nbt)));
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/RegionBasedStorage;write(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void removeResults(ChunkPos pos, StorageIoWorker.Result result, CallbackInfo ci) {
        if (!this.results.remove(pos, result)) { // Only write once
            ci.cancel();
        }
    }
}
