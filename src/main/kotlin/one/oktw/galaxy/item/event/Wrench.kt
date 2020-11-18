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
import net.minecraft.block.RepeaterBlock
import net.minecraft.block.TrappedChestBlock
import net.minecraft.block.WallMountedBlock.FACE
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.block.enums.ChestType
import net.minecraft.block.enums.RailShape
import net.minecraft.block.enums.SlabType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.property.Properties.*
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerSneakReleaseEvent
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ToolType
import one.oktw.galaxy.mixin.accessor.ShulkerBoxBlockEntityAccessor
import java.util.concurrent.ConcurrentHashMap

class Wrench {
    private val faceLock = ConcurrentHashMap<ServerPlayerEntity, BlockPos>()
    private val originalFace = ConcurrentHashMap<BlockPos, Direction>()

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

    @EventListener(true)
    fun onSneakRelease(event: PlayerSneakReleaseEvent) {
        if (faceLock.containsKey(event.player)) {
            if (originalFace.containsKey(faceLock[event.player])) originalFace.remove(faceLock[event.player])
            faceLock.remove(event.player)
        }
    }

    private fun wrenchSpin(event: PlayerUseItemOnBlock): Boolean {
        val world = event.context.world
        val player = event.context.player as ServerPlayerEntity
        val blockPos = event.context.blockPos
        val blockState = world.getBlockState(blockPos)
        val clickDirection = event.context.side

        // Check destructible
        if (blockState.getHardness(world, blockPos) < 0.0) return false
        if (((blockState.block == PISTON || blockState.block == STICKY_PISTON) && blockState.get(EXTENDED)) || blockState.block == PISTON_HEAD) return false
        if (blockState.contains(BED_PART)) return false


        if (faceLock.containsKey(player) && blockPos != faceLock[player]) {
            if (originalFace.containsKey(faceLock[player])) originalFace.remove(faceLock[player])
        }

        when {
            blockState.block == TORCH -> {
                world.setBlockState(
                    blockPos, WALL_TORCH.defaultState.with(
                        HORIZONTAL_FACING,
                        if (!faceLock.containsValue(blockPos) && clickDirection != Direction.UP && clickDirection != Direction.DOWN)
                            clickDirection else originalFace[blockPos] ?: Direction.NORTH
                    )
                )

                if (!faceLock.containsValue(blockPos)) originalFace[blockPos] = if (clickDirection != Direction.UP && clickDirection != Direction.DOWN)
                    clickDirection else Direction.NORTH
                faceLock[player] = blockPos
                return true
            }
            blockState.block == SOUL_TORCH -> {
                world.setBlockState(
                    blockPos, SOUL_WALL_TORCH.defaultState.with(
                        HORIZONTAL_FACING,
                        if (!faceLock.containsValue(blockPos) && clickDirection != Direction.UP && clickDirection != Direction.DOWN)
                            clickDirection else originalFace[blockPos] ?: Direction.NORTH
                    )
                )

                if (!faceLock.containsValue(blockPos)) originalFace[blockPos] = if (clickDirection != Direction.UP && clickDirection != Direction.DOWN)
                    clickDirection else Direction.NORTH
                faceLock[player] = blockPos
                return true
            }
            blockState.block == REDSTONE_TORCH -> {
                val newState = REDSTONE_WALL_TORCH.defaultState.with(
                    HORIZONTAL_FACING,
                    if (!faceLock.containsValue(blockPos) && clickDirection != Direction.UP && clickDirection != Direction.DOWN)
                        clickDirection else originalFace[blockPos] ?: Direction.NORTH
                )
                world.setBlockState(blockPos, newState)
                world.updateNeighborsAlways(blockPos, newState.block)
                world.updateNeighborsAlways(blockPos.offset(newState.get(HORIZONTAL_FACING)), blockState.block)

                if (!faceLock.containsValue(blockPos)) originalFace[blockPos] = if (clickDirection != Direction.UP && clickDirection != Direction.DOWN)
                    clickDirection else Direction.NORTH
                faceLock[player] = blockPos
                return true
            }
            blockState.contains(FACING) -> {
                val currentFacing = blockState.get(FACING)

                if (blockState.block == SHULKER_BOX) {
                    val entity = world.getBlockEntity(blockPos)
                    if (entity is ShulkerBoxBlockEntity) {
                        if ((entity as ShulkerBoxBlockEntityAccessor).animationStage != ShulkerBoxBlockEntity.AnimationStage.CLOSED) return false
                    }
                }

                if (currentFacing != clickDirection && !faceLock.containsValue(blockPos)) {
                    world.setBlockState(blockPos, blockState.with(FACING, clickDirection))
                } else {
                    world.setBlockState(
                        blockPos, blockState.with(
                            FACING,
                            (if (currentFacing != Direction.UP && currentFacing != Direction.DOWN) {
                                val toUpDownDirection = if (originalFace[blockPos] != Direction.UP && originalFace[blockPos] != Direction.DOWN)
                                    originalFace[blockPos] else Direction.NORTH
                                if (toUpDownDirection == currentFacing.rotateYClockwise()) {
                                    if (originalFace[blockPos] == Direction.DOWN) {
                                        Direction.DOWN
                                    } else {
                                        Direction.UP
                                    }
                                } else {
                                    currentFacing.rotateYClockwise()
                                }
                            } else if (currentFacing == Direction.UP) {
                                if (originalFace[blockPos] == Direction.DOWN) {
                                    if (originalFace[blockPos] != Direction.UP && originalFace[blockPos] != Direction.DOWN) {
                                        originalFace[blockPos]
                                    } else {
                                        Direction.NORTH
                                    }
                                } else {
                                    Direction.DOWN
                                }
                            } else {
                                if (!originalFace.containsKey(blockPos) && clickDirection == Direction.DOWN) {
                                    Direction.UP
                                } else if (originalFace[blockPos] == Direction.DOWN) {
                                    Direction.UP
                                } else {
                                    if (originalFace[blockPos] != Direction.UP && originalFace[blockPos] != Direction.DOWN) {
                                        originalFace[blockPos]
                                    } else {
                                        Direction.NORTH
                                    }
                                }
                            }) ?: rotateYClockwiseWithUpDown(currentFacing)
                        )
                    )
                }

                if (!faceLock.containsValue(blockPos)) originalFace[blockPos] = clickDirection
                faceLock[player] = blockPos
                return true
            }
            blockState.contains(HORIZONTAL_FACING) -> {
                val currentFacing = blockState.get(HORIZONTAL_FACING)

                if (blockState.contains(CHEST_TYPE)) {
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
                            return true
                        }
                        ChestType.SINGLE -> {
                            if (chestFacing == clickDirection) {
                                if (connectChest(event, chestFacing, blockPos, blockState, false)) return true
                            } else if (chestFacing.opposite == clickDirection) {
                                if (connectChest(event, chestFacing, blockPos, blockState, true)) return true
                            }
                        }
                        else -> Unit
                    }
                }

                val newDirection: Direction

                if (
                    currentFacing != clickDirection && clickDirection != Direction.UP &&
                    clickDirection != Direction.DOWN && !faceLock.containsValue(blockPos)
                ) {
                    world.setBlockState(blockPos, blockState.with(HORIZONTAL_FACING, clickDirection))
                    originalFace[blockPos] = clickDirection
                    faceLock[player] = blockPos
                    return true
                } else {
                    newDirection = blockState.get(HORIZONTAL_FACING).rotateYClockwise()
                }

                var newState = blockState.with(HORIZONTAL_FACING, newDirection)

                if (blockState.contains(BLOCK_HALF)) {
                    if (newDirection == originalFace[blockPos] ?: Direction.NORTH) {
                        newState = newState.cycle(BLOCK_HALF)
                    }
                }

                if (blockState.block == WALL_TORCH) {
                    if (newDirection == originalFace[blockPos] ?: Direction.NORTH) {
                        newState = TORCH.defaultState
                    }
                }

                if (blockState.block == SOUL_WALL_TORCH) {
                    if (newDirection == originalFace[blockPos] ?: Direction.NORTH) {
                        newState = SOUL_TORCH.defaultState
                    }
                }

                if (blockState.block == REDSTONE_WALL_TORCH) {
                    if (newDirection == originalFace[blockPos] ?: Direction.NORTH) {
                        newState = REDSTONE_TORCH.defaultState
                    }
                }

                if (blockState.contains(FACE)) {
                    if (newDirection == originalFace[blockPos] ?: Direction.NORTH) {
                        newState = newState.cycle(FACE)
                    }
                }

                if (blockState.contains(ATTACHMENT)) {
                    if (newDirection == originalFace[blockPos] ?: Direction.NORTH) {
                        newState = newState.cycle(ATTACHMENT)
                    }
                }

                if (blockState.block == REPEATER) {
                    newState = newState.with(LOCKED, (blockState.block as RepeaterBlock).isLocked(world, blockPos, newState))
                }

                if (!faceLock.containsValue(blockPos)) originalFace[blockPos] = if (clickDirection != Direction.UP && clickDirection != Direction.DOWN)
                    clickDirection else newDirection
                world.setBlockState(blockPos, newState)
                world.updateNeighborsAlways(blockPos, newState.block)
                if (newState.contains(HORIZONTAL_FACING)) {
                    world.updateNeighborsAlways(blockPos.offset(newState.get(HORIZONTAL_FACING)), newState.block)
                }
                faceLock[player] = blockPos
                return true
            }
            blockState.contains(HOPPER_FACING) -> {
                val currentFacing = blockState.get(HOPPER_FACING)

                if (currentFacing != clickDirection && clickDirection != Direction.UP && !faceLock.containsValue(blockPos)) {
                    world.setBlockState(blockPos, blockState.with(HOPPER_FACING, clickDirection))
                } else {
                    world.setBlockState(blockPos, blockState.with(HOPPER_FACING, rotateHopper(currentFacing, blockPos)))
                }
                if (!faceLock.containsValue(blockPos)) originalFace[blockPos] = if (clickDirection != Direction.UP)
                    clickDirection else rotateHopper(currentFacing, blockPos)
                faceLock[player] = blockPos
                return true
            }
            blockState.contains(RAIL_SHAPE) -> {
                val currentState = blockState.get(RAIL_SHAPE)
                world.setBlockState(blockPos, blockState.with(RAIL_SHAPE, spinRail(currentState)))
                return currentState != spinRail(currentState)
            }
            blockState.contains(STRAIGHT_RAIL_SHAPE) -> {
                val currentState = blockState.get(STRAIGHT_RAIL_SHAPE)
                world.setBlockState(blockPos, blockState.with(STRAIGHT_RAIL_SHAPE, spinRail(currentState)))
                return currentState != spinRail(currentState)
            }
            blockState.contains(AXIS) -> {
                world.setBlockState(blockPos, blockState.cycle(AXIS))
                return true
            }
            blockState.contains(ROTATION) -> {
                world.setBlockState(blockPos, blockState.cycle(ROTATION))
                return true
            }
            blockState.contains(SLAB_TYPE) -> {
                val direction = blockState.get(SLAB_TYPE)
                if (direction == SlabType.TOP) {
                    world.setBlockState(blockPos, blockState.with(SLAB_TYPE, SlabType.BOTTOM))
                } else if (direction == SlabType.BOTTOM) {
                    world.setBlockState(blockPos, blockState.with(SLAB_TYPE, SlabType.TOP))
                }
                return true
            }
        }

        return false
    }

    private fun rotateHopper(hopperDirection: Direction, blockPos: BlockPos): Direction = if (hopperDirection == Direction.DOWN) {
        (if (originalFace[blockPos] != Direction.DOWN) originalFace[blockPos] else Direction.NORTH) ?: Direction.NORTH
    } else {
        if (hopperDirection.rotateYClockwise() == (if (originalFace[blockPos] != Direction.DOWN) originalFace[blockPos] else Direction.NORTH) ?: Direction.NORTH) {
            Direction.DOWN
        } else {
            hopperDirection.rotateYClockwise()
        }
    }


    private fun connectChest(event: PlayerUseItemOnBlock, chestFacing: Direction, blockPos: BlockPos, blockState: BlockState, opposite: Boolean): Boolean {
        val world = event.context.world
        val facing = blockState.get(HORIZONTAL_FACING)
        val anotherPos = blockPos.offset(if (opposite) chestFacing.opposite else chestFacing)
        val anotherState = world.getBlockState(anotherPos)

        if (anotherState.block != blockState.block) return false

        if ((anotherState.block == CHEST || anotherState.block == TRAPPED_CHEST) && anotherState.get(CHEST_TYPE) == ChestType.SINGLE) {
            val anotherFacing = anotherState.get(HORIZONTAL_FACING)

            if (facing == anotherFacing) {
                world.setBlockState(blockPos, blockState.with(CHEST_TYPE, if (opposite) ChestType.LEFT else ChestType.RIGHT))
                world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, if (opposite) ChestType.RIGHT else ChestType.LEFT))
                return true
            }
        }

        return false
    }

    private fun rotateYClockwiseWithUpDown(direction: Direction) = when (direction) {
        Direction.NORTH -> Direction.EAST
        Direction.EAST -> Direction.SOUTH
        Direction.SOUTH -> Direction.WEST
        Direction.WEST -> Direction.UP
        Direction.UP -> Direction.DOWN
        Direction.DOWN -> Direction.NORTH
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
