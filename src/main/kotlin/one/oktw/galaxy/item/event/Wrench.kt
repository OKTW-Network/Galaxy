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
import net.minecraft.block.Blocks.*
import net.minecraft.block.ChestBlock
import net.minecraft.block.enums.ChestType
import net.minecraft.block.enums.SlabType
import net.minecraft.state.property.Properties.*
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ToolType

class Wrench {
    @EventListener(true)
    fun onUseItemOnBlock(event: PlayerUseItemOnBlock) {
        val player = event.context.player
        val hand = event.context.hand

        if (player != null) {
            if (player.getStackInHand(Hand.MAIN_HAND).isItemEqual(Tool(ToolType.WRENCH).createItemStack()) && hand == Hand.MAIN_HAND && player.isSneaking) {
                wrenchSpin(event)
            } else if (player.getStackInHand(Hand.OFF_HAND)
                    .isItemEqual(Tool(ToolType.WRENCH).createItemStack()) && player.mainHandStack.isEmpty && hand == Hand.OFF_HAND && player.isSneaking
            ) {
                wrenchSpin(event)
            }
        }
    }

    private fun wrenchSpin(event: PlayerUseItemOnBlock) {
        val blockPos = event.context.blockPos
        val blockState = event.context.world.getBlockState(blockPos)

        // Check destructible
        if (blockState.getHardness(event.context.world, blockPos) < 0.0) return
        if (((blockState.block == PISTON || blockState.block == STICKY_PISTON) && blockState.get(EXTENDED)) || blockState.block == PISTON_HEAD) return

        event.context.player?.swingHand(Hand.MAIN_HAND, true)

        if (blockState.block == CHEST) {
            when (blockState.get(CHEST_TYPE)) {
                ChestType.LEFT, ChestType.RIGHT -> {
                    val anotherPos = blockPos.offset(ChestBlock.getFacing(blockState))
                    val anotherState = event.context.world.getBlockState(anotherPos)
                    event.context.world.setBlockState(blockPos, blockState.with(CHEST_TYPE, ChestType.SINGLE))
                    event.context.world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, ChestType.SINGLE))
                }
                ChestType.SINGLE -> {
                    val chestDirection = ChestBlock.getFacing(blockState)
                    val clickDirection = event.context.side

                    if (clickDirection == Direction.UP || clickDirection == Direction.DOWN) {
                        event.context.world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, blockState.get(HORIZONTAL_FACING).rotateYClockwise()))
                    } else {
                        event.context.world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, clickDirection))
                    }

                    if (chestDirection == clickDirection) {
                        val facing = blockState.get(HORIZONTAL_FACING)
                        val anotherPos = blockPos.offset(ChestBlock.getFacing(blockState))
                        val anotherState = event.context.world.getBlockState(anotherPos)

                        if (anotherState.block == CHEST && anotherState.get(CHEST_TYPE) == ChestType.SINGLE) {
                            val anotherFacing = anotherState.get(HORIZONTAL_FACING)

                            if (anotherState.block == CHEST && facing == anotherFacing) {
                                event.context.world.setBlockState(blockPos, blockState.with(CHEST_TYPE, ChestType.RIGHT))
                                event.context.world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, ChestType.LEFT))
                            }
                        }
                    } else if (chestDirection.opposite == clickDirection) {
                        val facing = blockState.get(HORIZONTAL_FACING)
                        val anotherPos = blockPos.offset(chestDirection.opposite)
                        val anotherState = event.context.world.getBlockState(anotherPos)

                        if (anotherState.block == CHEST && anotherState.get(CHEST_TYPE) == ChestType.SINGLE) {
                            val anotherFacing = anotherState.get(HORIZONTAL_FACING)

                            if (facing == anotherFacing) {
                                event.context.world.setBlockState(blockPos, blockState.with(CHEST_TYPE, ChestType.LEFT))
                                event.context.world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, ChestType.RIGHT))
                            }
                        }
                    }
                }
                else -> Unit
            }
            return
        }

        var facing: BlockState

        facing = when {
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
