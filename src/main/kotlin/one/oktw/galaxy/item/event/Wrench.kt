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

package one.oktw.galaxy.item.event

import net.minecraft.block.BlockState
import net.minecraft.block.enums.ChestType
import net.minecraft.block.enums.SlabType
import net.minecraft.state.property.Properties.*
import net.minecraft.util.Hand
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ToolType

class Wrench {
    @EventListener(true)
    fun onUseItemOnBlock(event: PlayerUseItemOnBlock) {
        val player = event.context.player

        if (player != null) {
            if (player.getStackInHand(Hand.MAIN_HAND).isItemEqual(Tool(ToolType.WRENCH).createItemStack())) {
                wrenchSpin(event)
            } else if (player.getStackInHand(Hand.OFF_HAND).isItemEqual(Tool(ToolType.WRENCH).createItemStack()) && player.mainHandStack.isEmpty) {
                wrenchSpin(event)
            }
        }
    }

    private fun wrenchSpin(event: PlayerUseItemOnBlock) {
        val blockPos = event.context.blockPos
        val blockState = event.context.world.getBlockState(blockPos)
        var facing: BlockState

        facing = when {
            blockState.contains(CHEST_TYPE) -> if (blockState.get(CHEST_TYPE) == ChestType.SINGLE) blockState.cycle(HORIZONTAL_FACING) else blockState
            blockState.contains(FACING) -> blockState.cycle(FACING)
            blockState.contains(HOPPER_FACING) -> blockState.cycle(HOPPER_FACING)
            blockState.contains(HORIZONTAL_FACING) -> blockState.cycle(HORIZONTAL_FACING)
            blockState.contains(AXIS) -> blockState.cycle(AXIS)
            else -> blockState
        }

        if (blockState.contains(SLAB_TYPE)) {
            val direction = blockState.get(SLAB_TYPE)
            if (direction == SlabType.TOP) {
                facing = blockState.with(SLAB_TYPE, SlabType.BOTTOM)
            } else if (direction == SlabType.BOTTOM) {
                facing = blockState.with(SLAB_TYPE, SlabType.TOP)
            }
        }

        event.context.world.setBlockState(blockPos, facing)
    }
}
