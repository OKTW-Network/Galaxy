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

import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestSet;
import net.minecraft.world.poi.PointOfInterestStorage;
import one.oktw.galaxy.mixin.accessor.SerializingRegionBasedStorageAccessor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class MixinAsyncChunk_ThreadedAnvilChunkStorage {
    private final HashMap<ChunkPos, CompletableFuture<Void>> poiFutures = new HashMap<>();

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    ServerWorld world;
    @Shadow
    @Final
    private PointOfInterestStorage pointOfInterestStorage;
    @Shadow
    @Final
    private ThreadExecutor<Runnable> mainThreadExecutor;

    @Shadow
    private static boolean containsStatus(NbtCompound nbt) {
        return false;
    }

    @Shadow
    protected abstract CompletableFuture<Optional<NbtCompound>> getUpdatedChunkNbt(ChunkPos chunkPos);

    @Shadow
    protected abstract Chunk getProtoChunk(ChunkPos chunkPos);

    @Shadow
    protected abstract byte mark(ChunkPos pos, ChunkStatus.ChunkType type);

    @Shadow
    protected abstract Either<Chunk, ChunkHolder.Unloaded> recoverFromException(Throwable throwable, ChunkPos chunkPos);

    /**
     * @author James58899
     * @reason Async POI loading
     */
    @Overwrite
    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> loadChunk(ChunkPos pos) {
        CompletableFuture<Optional<NbtCompound>> chunkNbtFuture = this.getUpdatedChunkNbt(pos).thenApply(nbt -> nbt.filter(nbt2 -> {
            boolean bl = containsStatus(nbt2);
            if (!bl) {
                LOGGER.error("Chunk file at {} is missing level data, skipping", pos);
            }
            return bl;
        }));
        SerializingRegionBasedStorageAccessor poiStorage = ((SerializingRegionBasedStorageAccessor) pointOfInterestStorage);
        Optional<PointOfInterestSet> poiData = poiStorage.callGetIfLoaded(pos.toLong());
        var poiFuture = CompletableFuture.<Void>completedFuture(null);
        //noinspection OptionalAssignedToNull
        if (poiData == null || poiData.isEmpty()) {
            if (poiFutures.containsKey(pos)) {
                poiFuture = poiFutures.get(pos);
            } else {
                poiFuture = ((SerializingRegionBasedStorageAccessor) pointOfInterestStorage).callLoadNbt(pos).thenAcceptAsync(nbt -> {
                    RegistryOps<NbtElement> registryOps = RegistryOps.of(NbtOps.INSTANCE, world.getRegistryManager());
                    poiStorage.callUpdate(pos, registryOps, nbt.orElse(null));
                }, this.mainThreadExecutor);
                poiFutures.put(pos, poiFuture);
            }
        }
        return CompletableFuture.allOf(chunkNbtFuture, poiFuture).thenApplyAsync(unused -> {
            poiFutures.remove(pos);
            var nbt = chunkNbtFuture.join();
            this.world.getProfiler().visit("chunkLoad");
            if (nbt.isPresent()) {
                ProtoChunk chunk = ChunkSerializer.deserialize(this.world, this.pointOfInterestStorage, pos, nbt.get());
                this.mark(pos, ((Chunk) chunk).getStatus().getChunkType());
                return Either.<Chunk, ChunkHolder.Unloaded>left(chunk);
            }
            return Either.<Chunk, ChunkHolder.Unloaded>left(this.getProtoChunk(pos));
        }, this.mainThreadExecutor).exceptionallyAsync(throwable -> this.recoverFromException(throwable, pos), this.mainThreadExecutor);
    }
}
