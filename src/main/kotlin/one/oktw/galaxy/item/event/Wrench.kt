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
import net.minecraft.block.TrappedChestBlock
import net.minecraft.block.enums.ChestType
import net.minecraft.block.enums.SlabType
import net.minecraft.state.property.Properties.*
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
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
        if (blockState.contains(BED_PART)) return

        event.context.player?.swingHand(Hand.MAIN_HAND, true)

        if (blockState.block == CHEST || blockState.block == TRAPPED_CHEST) {
            val chestFacing: Direction = if (blockState.block == CHEST) {
                ChestBlock.getFacing(blockState)
            } else {
                TrappedChestBlock.getFacing(blockState)
            }

            when (blockState.get(CHEST_TYPE)) {
                ChestType.LEFT, ChestType.RIGHT -> {
                    val anotherPos = blockPos.offset(chestFacing)
                    val anotherState = event.context.world.getBlockState(anotherPos)
                    event.context.world.setBlockState(blockPos, blockState.with(CHEST_TYPE, ChestType.SINGLE))
                    event.context.world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, ChestType.SINGLE))
                }
                ChestType.SINGLE -> {
                    val clickDirection = event.context.side

                    chestRotate(event, clickDirection, blockPos, blockState)

                    if (chestFacing == clickDirection) {
                        val facing = blockState.get(HORIZONTAL_FACING)
                        val anotherPos = blockPos.offset(chestFacing)
                        val anotherState = event.context.world.getBlockState(anotherPos)

                        if ((anotherState.block == CHEST || anotherState.block == TRAPPED_CHEST) && anotherState.get(CHEST_TYPE) == ChestType.SINGLE) {
                            val anotherFacing = anotherState.get(HORIZONTAL_FACING)

                            if ((anotherState.block == CHEST || anotherState.block == TRAPPED_CHEST) && facing == anotherFacing) {
                                event.context.world.setBlockState(blockPos, blockState.with(CHEST_TYPE, ChestType.RIGHT))
                                event.context.world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, ChestType.LEFT))
                            }
                        }
                    } else if (chestFacing.opposite == clickDirection) {
                        val facing = blockState.get(HORIZONTAL_FACING)
                        val anotherPos = blockPos.offset(chestFacing.opposite)
                        val anotherState = event.context.world.getBlockState(anotherPos)

                        if ((anotherState.block == CHEST || anotherState.block == TRAPPED_CHEST) && anotherState.get(CHEST_TYPE) == ChestType.SINGLE) {
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

        if (blockState.block == ENDER_CHEST) {
            val clickDirection = event.context.side
            chestRotate(event, clickDirection, blockPos, blockState)
            return
        }

        if (blockState.block == HOPPER) {
            val clickDirection = event.context.side

            if (clickDirection == Direction.UP) {
                if (blockState.get(HOPPER_FACING) == Direction.DOWN) {
                    event.context.world.setBlockState(blockPos, blockState.with(HOPPER_FACING, Direction.NORTH))
                } else {
                    if (blockState.get(HOPPER_FACING) == Direction.WEST) {
                        event.context.world.setBlockState(blockPos, blockState.with(HOPPER_FACING, Direction.DOWN))
                    } else {
                        event.context.world.setBlockState(blockPos, blockState.with(HOPPER_FACING, blockState.get(HOPPER_FACING).rotateYClockwise()))
                    }
                }
            } else if (clickDirection == Direction.DOWN){
                event.context.world.setBlockState(blockPos, blockState.with(HOPPER_FACING, Direction.DOWN))
            } else {
                if (blockState.get(HOPPER_FACING) == clickDirection){
                    val facing = when (clickDirection) {
                        Direction.NORTH -> Direction.SOUTH
                        Direction.SOUTH -> Direction.NORTH
                        Direction.WEST -> Direction.EAST
                        Direction.EAST -> Direction.WEST
                        else -> return
                    }
                    event.context.world.setBlockState(blockPos, blockState.with(HOPPER_FACING, facing))
                } else {
                    event.context.world.setBlockState(blockPos, blockState.with(HOPPER_FACING, clickDirection))
                }
            }
            return
        }

        var facing: BlockState

        facing = when {
            blockState.contains(FACING) -> blockState.cycle(FACING)
            blockState.contains(HORIZONTAL_FACING) -> blockState.cycle(HORIZONTAL_FACING)
            blockState.contains(AXIS) -> blockState.cycle(AXIS)
            blockState.contains(ROTATION) -> blockState.cycle(ROTATION)
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

    private fun chestRotate(event: PlayerUseItemOnBlock, clickDirection: Direction, blockPos: BlockPos, blockState: BlockState) {
        if (clickDirection == Direction.UP || clickDirection == Direction.DOWN) {
            event.context.world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, blockState.get(HORIZONTAL_FACING).rotateYClockwise()))
        } else {
            if (blockState.get(HORIZONTAL_FACING) == clickDirection){
                val facing = when (clickDirection) {
                    Direction.NORTH -> Direction.SOUTH
                    Direction.SOUTH -> Direction.NORTH
                    Direction.WEST -> Direction.EAST
                    Direction.EAST -> Direction.WEST
                    else -> return
                }
                event.context.world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, facing))
            } else {
                event.context.world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, clickDirection))
            }
        }
    }
}