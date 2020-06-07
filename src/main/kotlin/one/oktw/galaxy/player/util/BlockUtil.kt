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

package one.oktw.galaxy.player.util

import net.minecraft.block.*
import net.minecraft.block.Blocks.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

object BlockUtil {
    fun isMature(world: ServerWorld, blockPos: BlockPos, blockState: BlockState): Boolean = when (blockState.block) {
        WHEAT, CARROTS, POTATOES, BEETROOTS -> blockState.let((blockState.block as CropBlock)::isMature)
        COCOA -> blockState[CocoaBlock.AGE] >= 2
        NETHER_WART -> blockState[NetherWartBlock.AGE] >= 3
        MELON -> isNextTo(world, blockPos, ATTACHED_MELON_STEM)
        PUMPKIN -> isNextTo(world, blockPos, ATTACHED_PUMPKIN_STEM)
        else -> false
    }

    private fun isNextTo(world: ServerWorld, blockPos: BlockPos, block: Block): Boolean {
        return world.getBlockState(blockPos.add(1, 0, 0)).block == block ||
            world.getBlockState(blockPos.add(0, 0, 1)).block == block ||
            world.getBlockState(blockPos.add(-1, 0, 0)).block == block ||
            world.getBlockState(blockPos.add(0, 0, -1)).block == block
    }
}
