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

package one.oktw.galaxy.block.event

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Position
import net.minecraft.util.math.Vec3d
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.util.CustomBlockUtil
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerJumpEvent
import one.oktw.galaxy.event.type.PlayerSneakEvent

class ElevatorEvents {
    private fun canWeTeleport(player: ServerPlayerEntity, position: Position): Boolean {
        return listOf<Block>(Blocks.AIR, Blocks.WATER).contains(player.serverWorld.getBlockState(BlockPos(position)).block)
    }

    private fun isElevator(player: ServerPlayerEntity, position: Position): Boolean {
        return CustomBlockUtil.positionMatchesCustomBlock(player.serverWorld, BlockPos(position), BlockType.ELEVATOR)
    }

    @EventListener(sync = true)
    fun onJump(event: PlayerJumpEvent) {
        val player = event.player
        val playerPosition = player.pos
        val currentElevatorPosition: Vec3d = playerPosition.subtract(0.0, 1.0, 0.0)
        var nextElevatorPosition: Vec3d

        if (isElevator(player, currentElevatorPosition) && canWeTeleport(player, playerPosition)) {
            for (i in 2..8) {
                nextElevatorPosition = currentElevatorPosition.add(0.0, i.toDouble(), 0.0)
                if (isElevator(player, nextElevatorPosition) && canWeTeleport(player, nextElevatorPosition.add(0.0, 1.0, 0.0))) {
                    event.player.requestTeleport(nextElevatorPosition.x, nextElevatorPosition.y + 1, nextElevatorPosition.z)
                    event.player.world.playSound(
                        null,
                        BlockPos(nextElevatorPosition),
                        SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                        SoundCategory.BLOCKS,
                        1.0f,
                        1.0f
                    )
                    break
                }
            }
        }
    }

    @EventListener(sync = true)
    fun onSneak(event: PlayerSneakEvent) {
        val player = event.player
        val playerPosition = player.pos
        val currentElevatorPosition: Vec3d = playerPosition.subtract(0.0, 1.0, 0.0)
        var nextElevatorPosition: Vec3d

        if (isElevator(player, currentElevatorPosition) && canWeTeleport(player, playerPosition)) {
            for (i in 2..8) {
                nextElevatorPosition = currentElevatorPosition.subtract(0.0, i.toDouble(), 0.0)
                if (isElevator(player, nextElevatorPosition) && canWeTeleport(player, nextElevatorPosition.add(0.0, 1.0, 0.0))) {
                    event.player.requestTeleport(nextElevatorPosition.x, nextElevatorPosition.y + 1, nextElevatorPosition.z)
                    event.player.world.playSound(
                        null,
                        BlockPos(nextElevatorPosition),
                        SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                        SoundCategory.BLOCKS,
                        1.0f,
                        1.0f
                    )
                    break
                }
            }
        }
    }

}
