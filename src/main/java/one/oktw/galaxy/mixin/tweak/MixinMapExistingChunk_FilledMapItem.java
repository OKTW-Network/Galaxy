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

import net.minecraft.item.FilledMapItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FilledMapItem.class)
public abstract class MixinMapExistingChunk_FilledMapItem {
    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getWorldChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/WorldChunk;"))
    private WorldChunk getExistingChunk(World world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        WorldChunk chunk = (WorldChunk) world.getExistingChunk(chunkPos.x, chunkPos.z);
        if (chunk == null) chunk = new EmptyChunk(world, chunkPos);
        return chunk;
    }
}
