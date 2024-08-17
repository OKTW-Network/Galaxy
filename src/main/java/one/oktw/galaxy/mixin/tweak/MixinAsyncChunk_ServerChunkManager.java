/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2024
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

import net.minecraft.server.world.*;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import one.oktw.galaxy.mixin.accessor.ServerChunkLoadingManagerAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(ServerChunkManager.class)
public abstract class MixinAsyncChunk_ServerChunkManager {
    @Shadow
    @Final
    ServerWorld world;

    @Shadow
    @Nullable
    protected abstract ChunkHolder getChunkHolder(long pos);

    @Shadow
    protected abstract boolean isMissingForLevel(@Nullable ChunkHolder holder, int maxLevel);

    /**
     * @author James58899
     * @reason Use static ChunkPos.toLong
     */
    @Overwrite
    public boolean isChunkLoaded(int x, int z) {
        return !this.isMissingForLevel(this.getChunkHolder(ChunkPos.toLong(x, z)), ChunkLevels.getLevelFromStatus(ChunkStatus.FULL));
    }

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkLoadingManager;entryIterator()Ljava/lang/Iterable;"))
    private Iterable<ChunkHolder> earlyCheckChunkShouldTick(ServerChunkLoadingManager instance) {
        ServerChunkLoadingManagerAccessor accessor = (ServerChunkLoadingManagerAccessor) instance;
        var stream = StreamSupport.stream(accessor.callEntryIterator().spliterator(), false);
        return stream.filter(chunkHolder -> {
            WorldChunk chunk = chunkHolder.getWorldChunk();
            if (chunk == null) return false;
            ChunkPos pos = chunk.getPos();
            return world.shouldTick(pos) && accessor.callShouldTick(pos);
        }).collect(Collectors.toList());
    }

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;shouldTick(Lnet/minecraft/util/math/ChunkPos;)Z"))
    private boolean skipDupTickCheck(ServerWorld instance, ChunkPos pos) {
        return true;
    }

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkLoadingManager;shouldTick(Lnet/minecraft/util/math/ChunkPos;)Z"))
    private boolean skipDupTickCheck(ServerChunkLoadingManager instance, ChunkPos pos) {
        return true;
    }
}
