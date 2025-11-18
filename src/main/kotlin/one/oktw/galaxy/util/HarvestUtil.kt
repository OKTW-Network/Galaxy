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

package one.oktw.galaxy.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.IntegerProperty

object HarvestUtil {
    fun isMature(world: ServerLevel, blockPos: BlockPos, blockState: BlockState): Boolean = when (blockState.block) {
        is CropBlock -> (blockState.block as CropBlock).isMaxAge(blockState)
        Blocks.COCOA -> blockState.getValue(CocoaBlock.AGE) >= 2
        Blocks.NETHER_WART -> blockState.getValue(NetherWartBlock.AGE) >= 3
        Blocks.MELON -> isNextTo(world, blockPos, Blocks.ATTACHED_MELON_STEM)
        Blocks.PUMPKIN -> isNextTo(world, blockPos, Blocks.ATTACHED_PUMPKIN_STEM)
        else -> false
    }

    fun getAgeProp(block: Block): IntegerProperty? = when (block) {
        Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES -> CropBlock.AGE
        Blocks.BEETROOTS -> BeetrootBlock.AGE
        Blocks.COCOA -> CocoaBlock.AGE
        Blocks.NETHER_WART -> NetherWartBlock.AGE
        Blocks.PUMPKIN, Blocks.MELON -> null
        else -> null
    }

    private fun isNextTo(world: ServerLevel, blockPos: BlockPos, block: Block): Boolean {
        for (direction in arrayOf(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)) {
            val pos = blockPos.relative(direction)
            val blockState = world.getBlockState(pos)
            if (blockState.block == block && pos.relative(blockState.getValue(HORIZONTAL_FACING)) == blockPos) return true
        }
        return false
    }
}
