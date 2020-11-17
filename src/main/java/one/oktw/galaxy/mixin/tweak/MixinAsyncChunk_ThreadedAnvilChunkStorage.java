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

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import one.oktw.galaxy.mixin.accessor.AsyncChunk_VersionedChunkStorage;
import one.oktw.galaxy.mixin.interfaces.AsyncChunk_StorageIoWorker;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.IOException;
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
    private ServerWorld world;
    @Shadow
    @Final
    private Supplier<PersistentStateManager> persistentStateManagerFactory;
    @Shadow
    @Final
    private StructureManager structureManager;

    @Shadow
    @Final
    private PointOfInterestStorage pointOfInterestStorage;

    public MixinAsyncChunk_ThreadedAnvilChunkStorage(File file, DataFixer dataFixer, boolean bl) {
        super(file, dataFixer, bl);
    }

    @Shadow
    protected abstract void method_27054(ChunkPos chunkPos);

    @Shadow
    protected abstract byte method_27053(ChunkPos chunkPos, ChunkStatus.ChunkType chunkType);

    /**
     * @author James58899
     * @reason Async chunk load
     */
    @Overwrite
    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> loadChunk(ChunkPos pos) {
        return getUpdatedChunkTagAsync(pos).handleAsync((compoundTag, t) -> {
            try {
                this.world.getProfiler().visit("chunkLoad");

                if (t != null) {
                    throw (Exception) t;
                }

                if (compoundTag != null) {
                    boolean bl = compoundTag.contains("Level", 10) && compoundTag.getCompound("Level").contains("Status", 8);
                    if (bl) {
                        Chunk chunk = ChunkSerializer.deserialize(this.world, this.structureManager, this.pointOfInterestStorage, pos, compoundTag);
                        chunk.setLastSaveTime(this.world.getTime());
                        this.method_27053(pos, chunk.getStatus().getChunkType());
                        return Either.left(chunk);
                    }

                    LOGGER.error("Chunk file at {} is missing level data, skipping", pos);
                }
            } catch (CrashException var5) {
                Throwable throwable = var5.getCause();
                if (!(throwable instanceof IOException)) {
                    this.method_27054(pos);
                    throw var5;
                }

                LOGGER.error("Couldn't load chunk {}", pos, throwable);
            } catch (Exception var6) {
                LOGGER.error("Couldn't load chunk {}", pos, var6);
            }

            this.method_27054(pos);
            return Either.left(new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA));
        }, this.mainThreadExecutor);
    }

    private CompletableFuture<CompoundTag> getUpdatedChunkTagAsync(ChunkPos pos) {
        return ((AsyncChunk_StorageIoWorker) ((AsyncChunk_VersionedChunkStorage) this).getWorker()).getNbt(pos)
            .thenApplyAsync(compoundTag -> compoundTag == null ? null : this.updateChunkTag(world.getRegistryKey(), this.persistentStateManagerFactory, compoundTag), mainThreadExecutor);
    }
}
