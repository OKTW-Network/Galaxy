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

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import one.oktw.galaxy.mixin.accessor.AsyncChunk_VersionedChunkStorage;
import one.oktw.galaxy.mixin.accessor.StorageIoWorkerAccessor;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class MixinAsyncChunk_ThreadedAnvilChunkStorage extends VersionedChunkStorage {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private ThreadExecutor<Runnable> mainThreadExecutor;
    @Shadow
    @Final
    ServerWorld world;
    @Shadow
    @Final
    private Supplier<PersistentStateManager> persistentStateManagerFactory;
    @Shadow
    @Final
    private PointOfInterestStorage pointOfInterestStorage;
    @Shadow
    private ChunkGenerator chunkGenerator;

    @Shadow
    protected abstract byte mark(ChunkPos pos, ChunkStatus.ChunkType type);

    @Shadow
    protected abstract void markAsProtoChunk(ChunkPos pos);

    public MixinAsyncChunk_ThreadedAnvilChunkStorage(Path directory, DataFixer dataFixer, boolean dsync) {
        super(directory, dataFixer, dsync);
    }

    /**
     * @author James58899
     * @reason Async chunk load
     */
    @Overwrite
    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> loadChunk(ChunkPos pos) {
        world.getProfiler().visit("chunkLoad");
        return getUpdatedChunkNbtAsync(pos).handleAsync((nbt, t) -> {
            try {
                if (t != null) {
                    throw (Exception) t;
                }

                if (nbt != null) {
                    boolean bl = nbt.contains("Status", 8);
                    if (bl) {
                        Chunk chunk = ChunkSerializer.deserialize(world, pointOfInterestStorage, pos, nbt);
                        mark(pos, chunk.getStatus().getChunkType());
                        return Either.left(chunk);
                    }

                    LOGGER.error("Chunk file at {} is missing level data, skipping", pos);
                }
            } catch (CrashException e) {
                Throwable throwable = e.getCause();
                if (throwable instanceof IOException) {
                    LOGGER.error("Couldn't load chunk {}", pos, throwable);
                }
                this.markAsProtoChunk(pos);
                throw e;
            } catch (Exception e) {
                LOGGER.error("Couldn't load chunk {}", pos, e);
            }

            markAsProtoChunk(pos);
            return Either.left(new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, world, world.getRegistryManager().get(Registry.BIOME_KEY), null));
        }, mainThreadExecutor);
    }

    private CompletableFuture<NbtCompound> getUpdatedChunkNbtAsync(ChunkPos pos) {
        return ((StorageIoWorkerAccessor) ((AsyncChunk_VersionedChunkStorage) this).getWorker()).callReadChunkData(pos)
            .thenApplyAsync(nbt -> nbt == null ? null : updateChunkNbt(world.getRegistryKey(), persistentStateManagerFactory, nbt, chunkGenerator.getCodecKey()),
                Util.getMainWorkerExecutor());
    }
}
