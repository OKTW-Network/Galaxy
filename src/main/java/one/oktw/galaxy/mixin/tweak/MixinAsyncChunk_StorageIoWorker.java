/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
import one.oktw.galaxy.mixin.interfaces.AsyncChunk_StorageIoWorker;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mixin(StorageIoWorker.class)
public abstract class MixinAsyncChunk_StorageIoWorker implements AsyncChunk_StorageIoWorker {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private RegionBasedStorage storage;
    @Shadow
    @Final
    private Map<ChunkPos, StorageIoWorker.Result> results;

    @Shadow
    protected abstract <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> supplier);

    @Nullable
    @Override
    public CompletableFuture<CompoundTag> getNbt(ChunkPos pos) {
        return run(() -> {
            var result = this.results.get(pos);
            if (result != null) {
                return Either.left(result.nbt);
            } else {
                try {
                    CompoundTag compoundTag = storage.getTagAt(pos);
                    return Either.left(compoundTag);
                } catch (Exception var4) {
                    LOGGER.warn("Failed to read chunk {}", pos, var4);
                    return Either.right(var4);
                }
            }
        });
    }
}
