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

package one.oktw.galaxy.util

import net.minecraft.block.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties.HORIZONTAL_FACING
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

object HarvestUtil {
    fun isMature(world: ServerWorld, blockPos: BlockPos, blockState: BlockState): Boolean = when (blockState.block) {
        Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS -> blockState.let((blockState.block as CropBlock)::isMature)
        Blocks.COCOA -> blockState[CocoaBlock.AGE] >= 2
        Blocks.NETHER_WART -> blockState[NetherWartBlock.AGE] >= 3
        Blocks.MELON -> isNextTo(world, blockPos, Blocks.ATTACHED_MELON_STEM)
        Blocks.PUMPKIN -> isNextTo(world, blockPos, Blocks.ATTACHED_PUMPKIN_STEM)
        else -> false
    }

    private fun isNextTo(world: ServerWorld, blockPos: BlockPos, block: Block): Boolean {
        for (direction in arrayOf(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)) {
            val pos = blockPos.offset(direction)
            val blockState = world.getBlockState(pos)
            if (blockState.block == block && pos.offset(blockState.get(HORIZONTAL_FACING)) == blockPos) return true
        }
        return false
    }
}
