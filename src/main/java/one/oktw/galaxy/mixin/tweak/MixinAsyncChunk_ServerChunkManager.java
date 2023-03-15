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

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import one.oktw.galaxy.mixin.accessor.ThreadedAnvilChunkStorageAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(ServerChunkManager.class)
public class MixinAsyncChunk_ServerChunkManager {
    @Shadow
    @Final
    ServerWorld world;

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;entryIterator()Ljava/lang/Iterable;"))
    private Iterable<ChunkHolder> earlyCheckChunkShouldTick(ThreadedAnvilChunkStorage instance) {
        ThreadedAnvilChunkStorageAccessor accessor = (ThreadedAnvilChunkStorageAccessor) instance;
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

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;shouldTick(Lnet/minecraft/util/math/ChunkPos;)Z"))
    private boolean skipDupTickCheck(ThreadedAnvilChunkStorage instance, ChunkPos pos) {
        return true;
    }
}
