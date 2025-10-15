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

import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.PrioritizedConsecutiveExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.StorageIoWorker.Priority;
import net.minecraft.world.storage.StorageKey;
import one.oktw.galaxy.util.KotlinCoroutineTaskExecutor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.SequencedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(StorageIoWorker.class)
public abstract class MixinAsyncChunk_StorageIoWorker {
    @Unique
    private final AtomicBoolean writeLock = new AtomicBoolean(false);

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


    @Inject(method = "write", at = @At(value = "HEAD"), cancellable = true)
    private void removeResults(ChunkPos pos, StorageIoWorker.Result result, CallbackInfo ci) {
        if (!this.results.remove(pos, result)) { // Only write once
            ci.cancel();
        }
    }
}
