/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.StorageIoWorker;
import one.oktw.galaxy.util.KotlinCoroutineTaskExecutor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Map;
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

    @Shadow
    protected abstract void writeRemainingResults();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void parallelExecutor(File directory, boolean dsync, String name, CallbackInfo ci) {
        results = new ConcurrentHashMap<>();
        executor = new KotlinCoroutineTaskExecutor<>(new TaskQueue.Prioritized(4 /* FOREGROUND,BACKGROUND,WRITE_DONE,SHUTDOWN */), "IOWorker-" + name);
    }

    /**
     * @author James58899
     * @reason null check and delay remove
     */
    @Overwrite
    private void writeResult() {
        if (!this.results.isEmpty() && !writeLock.getAndSet(true)) {
            results.forEach((chunkPos, result) -> executor.send(new TaskQueue.PrioritizedTask(1 /* BACKGROUND */, () -> write(chunkPos, result))));
            this.executor.send(new TaskQueue.PrioritizedTask(2 /* WRITE_DONE */, () -> {
                writeLock.set(false);
                writeRemainingResults();
            }));
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/RegionBasedStorage;write(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)V"))
    private void removeResults(ChunkPos pos, StorageIoWorker.Result result, CallbackInfo ci) {
        this.results.remove(pos, result);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "method_27938", at = @At(value = "NEW", target = "net/minecraft/util/thread/TaskQueue$PrioritizedTask"))
    private static TaskQueue.PrioritizedTask changeShutdownPriority(int priority, Runnable runnable) {
        return new TaskQueue.PrioritizedTask(3 /* SHUTDOWN */, runnable);
    }
}
