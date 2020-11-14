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
import net.minecraft.block.WallMountedBlock.FACE
import net.minecraft.block.enums.ChestType
import net.minecraft.block.enums.RailShape
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
            if (
                player.getStackInHand(Hand.MAIN_HAND).isItemEqual(Tool(ToolType.WRENCH).createItemStack()) &&
                hand == Hand.MAIN_HAND && player.shouldCancelInteraction()
            ) {
                if (wrenchSpin(event)) player.swingHand(Hand.MAIN_HAND, true)
            } else if (
                player.getStackInHand(Hand.OFF_HAND).isItemEqual(Tool(ToolType.WRENCH).createItemStack()) &&
                player.mainHandStack.isEmpty && hand == Hand.OFF_HAND && player.shouldCancelInteraction()
            ) {
                if (wrenchSpin(event)) player.swingHand(Hand.OFF_HAND, true)
            }
        }
    }

    private fun wrenchSpin(event: PlayerUseItemOnBlock): Boolean {
        val world = event.context.world
        val blockPos = event.context.blockPos
        val blockState = world.getBlockState(blockPos)

        // Check destructible
        if (blockState.getHardness(world, blockPos) < 0.0) return false
        if (((blockState.block == PISTON || blockState.block == STICKY_PISTON) && blockState.get(EXTENDED)) || blockState.block == PISTON_HEAD) return false
        if (blockState.contains(BED_PART)) return false

        if (blockState.block == CHEST || blockState.block == TRAPPED_CHEST) {
            val chestFacing: Direction = if (blockState.block == CHEST) {
                ChestBlock.getFacing(blockState)
            } else {
                TrappedChestBlock.getFacing(blockState)
            }

            when (blockState.get(CHEST_TYPE)) {
                ChestType.LEFT, ChestType.RIGHT -> {
                    val anotherPos = blockPos.offset(chestFacing)
                    val anotherState = world.getBlockState(anotherPos)
                    world.setBlockState(blockPos, blockState.with(CHEST_TYPE, ChestType.SINGLE))
                    world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, ChestType.SINGLE))
                }
                ChestType.SINGLE -> {
                    val clickDirection = event.context.side

                    chestRotate(event, clickDirection, blockPos, blockState)

                    if (chestFacing == clickDirection) {
                        connectChest(event, chestFacing, blockPos, blockState, false)
                    } else if (chestFacing.opposite == clickDirection) {
                        connectChest(event, chestFacing, blockPos, blockState, true)
                    }
                }
                else -> Unit
            }
            return true
        }

        if (blockState.block == ENDER_CHEST) {
            val clickDirection = event.context.side
            chestRotate(event, clickDirection, blockPos, blockState)
            return true
        }

        if (blockState.block == HOPPER) {
            val clickDirection = event.context.side

            if (clickDirection == Direction.UP) {
                if (blockState.get(HOPPER_FACING) == Direction.DOWN) {
                    world.setBlockState(blockPos, blockState.with(HOPPER_FACING, Direction.NORTH))
                } else {
                    if (blockState.get(HOPPER_FACING) == Direction.WEST) {
                        world.setBlockState(blockPos, blockState.with(HOPPER_FACING, Direction.DOWN))
                    } else {
                        world.setBlockState(blockPos, blockState.with(HOPPER_FACING, blockState.get(HOPPER_FACING).rotateYClockwise()))
                    }
                }
            } else if (clickDirection == Direction.DOWN) {
                world.setBlockState(blockPos, blockState.with(HOPPER_FACING, Direction.DOWN))
            } else {
                if (blockState.get(HOPPER_FACING) == clickDirection) {
                    world.setBlockState(blockPos, blockState.with(HOPPER_FACING, spinToOpposite(clickDirection)))
                } else {
                    world.setBlockState(blockPos, blockState.with(HOPPER_FACING, clickDirection))
                }
            }
            return true
        }

        if (blockState.contains(BLOCK_HALF)) {
            val newDirection = blockState.get(HORIZONTAL_FACING).rotateYClockwise()
            var newState = blockState.with(HORIZONTAL_FACING, newDirection)
            if (newDirection == Direction.NORTH) {
                newState = newState.cycle(BLOCK_HALF)
            }
            world.setBlockState(blockPos, newState)
            return true
        }

        if (blockState.contains(RAIL_SHAPE)) {
            val currentState = blockState.get(RAIL_SHAPE)
            world.setBlockState(blockPos, blockState.with(RAIL_SHAPE, spinRail(currentState)))
            return currentState != spinRail(currentState)
        } else if (blockState.contains(STRAIGHT_RAIL_SHAPE)) {
            val currentState = blockState.get(STRAIGHT_RAIL_SHAPE)
            world.setBlockState(blockPos, blockState.with(STRAIGHT_RAIL_SHAPE, spinRail(currentState)))
            return currentState != spinRail(currentState)
        }

        if (blockState.block == GRINDSTONE) {
            val newDirection = blockState.get(HORIZONTAL_FACING).rotateYClockwise()
            var newState = blockState.with(HORIZONTAL_FACING, newDirection)
            if (newDirection == Direction.NORTH) {
                newState = newState.cycle(FACE)
            }
            world.setBlockState(blockPos, newState)
            return true
        }

        if (blockState.block == BELL) {
            val newDirection = blockState.get(HORIZONTAL_FACING).rotateYClockwise()
            var newState = blockState.with(HORIZONTAL_FACING, newDirection)
            if (newDirection == Direction.NORTH) {
                newState = newState.cycle(ATTACHMENT)
            }
            world.setBlockState(blockPos, newState)
            return true
        }

        when {
            blockState.contains(FACING) -> {
                world.setBlockState(blockPos, blockState.cycle(FACING))
                return true
            }
            blockState.contains(HORIZONTAL_FACING) -> {
                val newState = blockState.cycle(HORIZONTAL_FACING)
                world.setBlockState(blockPos, newState)
                if (blockState.block == REPEATER || blockState.block == COMPARATOR) {
                    world.updateNeighborsAlways(blockPos, blockState.block)
                    if ((blockState.block == REPEATER && !blockState.get(LOCKED)) || blockState.block == COMPARATOR) {
                        world.updateNeighborsAlways(blockPos.offset(newState.get(HORIZONTAL_FACING)), blockState.block)
                    }
                }
                return true
            }
            blockState.contains(AXIS) -> {
                world.setBlockState(blockPos, blockState.cycle(AXIS))
                return true
            }
            blockState.contains(ROTATION) -> {
                world.setBlockState(blockPos, blockState.cycle(ROTATION))
                return true
            }
        }

        if (blockState.contains(SLAB_TYPE)) {
            val direction = blockState.get(SLAB_TYPE)
            if (direction == SlabType.TOP) {
                world.setBlockState(blockPos, blockState.with(SLAB_TYPE, SlabType.BOTTOM))
            } else if (direction == SlabType.BOTTOM) {
                world.setBlockState(blockPos, blockState.with(SLAB_TYPE, SlabType.TOP))
            }
            return true
        }

        return false
    }

    private fun chestRotate(event: PlayerUseItemOnBlock, clickDirection: Direction, blockPos: BlockPos, blockState: BlockState) {
        val world = event.context.world
        if (clickDirection == Direction.UP || clickDirection == Direction.DOWN) {
            world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, blockState.get(HORIZONTAL_FACING).rotateYClockwise()))
        } else {
            if (blockState.get(HORIZONTAL_FACING) == clickDirection) {
                world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, spinToOpposite(clickDirection)))
            } else {
                world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, clickDirection))
            }
        }
    }

    private fun connectChest(event: PlayerUseItemOnBlock, chestFacing: Direction, blockPos: BlockPos, blockState: BlockState, opposite: Boolean) {
        val world = event.context.world
        val facing = blockState.get(HORIZONTAL_FACING)
        val anotherPos = blockPos.offset(if (opposite) chestFacing.opposite else chestFacing)
        val anotherState = world.getBlockState(anotherPos)

        if (anotherState.block != blockState.block) return

        if ((anotherState.block == CHEST || anotherState.block == TRAPPED_CHEST) && anotherState.get(CHEST_TYPE) == ChestType.SINGLE) {
            val anotherFacing = anotherState.get(HORIZONTAL_FACING)

            if (facing == anotherFacing) {
                world.setBlockState(blockPos, blockState.with(CHEST_TYPE, if (opposite) ChestType.LEFT else ChestType.RIGHT))
                world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, if (opposite) ChestType.RIGHT else ChestType.LEFT))
            }
        }
    }

    private fun spinToOpposite(clickDirection: Direction) = when (clickDirection) {
        Direction.NORTH -> Direction.SOUTH
        Direction.SOUTH -> Direction.NORTH
        Direction.WEST -> Direction.EAST
        Direction.EAST -> Direction.WEST
        else -> clickDirection
    }

    private fun spinRail(shape: RailShape) = when (shape) {
        RailShape.NORTH_SOUTH -> RailShape.EAST_WEST
        RailShape.EAST_WEST -> RailShape.NORTH_SOUTH
        RailShape.SOUTH_WEST -> RailShape.NORTH_WEST
        RailShape.NORTH_WEST -> RailShape.NORTH_EAST
        RailShape.NORTH_EAST -> RailShape.SOUTH_EAST
        RailShape.SOUTH_EAST -> RailShape.SOUTH_WEST
        else -> shape
    }
}
