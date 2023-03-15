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

import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.StorageIoWorker.Priority;
import one.oktw.galaxy.util.KotlinCoroutineTaskExecutor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(StorageIoWorker.class)
public abstract class MixinAsyncChunk_StorageIoWorker {
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
    private void parallelExecutor(Path directory, boolean dsync, String name, CallbackInfo ci) {
        results = new ConcurrentHashMap<>();
        executor = new KotlinCoroutineTaskExecutor<>(new TaskQueue.Prioritized(Priority.values().length), "IOWorker-" + name);
    }

    /**
     * @author James58899
     * @reason null check and delay remove
     */
    @Overwrite
    private void writeResult() {
        if (!this.results.isEmpty()) {
            HashMap<Long, ArrayList<Pair<ChunkPos, StorageIoWorker.Result>>> map = new HashMap<>();
            results.forEach((pos, result) -> map.computeIfAbsent(ChunkPos.toLong(pos.getRegionX(), pos.getRegionZ()), k -> new ArrayList<>()).add(new Pair<>(pos, result)));
            map.values().forEach(list ->
                executor.send(new TaskQueue.PrioritizedTask(Priority.FOREGROUND.ordinal(), () -> list.forEach(pair -> write(pair.getLeft(), pair.getRight()))))
            );
            this.executor.send(new TaskQueue.PrioritizedTask(Priority.BACKGROUND.ordinal(), this::writeResult));
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/RegionBasedStorage;write(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)V"), cancellable = true)
    private void removeResults(ChunkPos pos, StorageIoWorker.Result result, CallbackInfo ci) {
        if (!this.results.remove(pos, result)) { // Only write once
            ci.cancel();
        }
    }
}
