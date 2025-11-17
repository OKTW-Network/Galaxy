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

package one.oktw.galaxy.block.event

import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import one.oktw.galaxy.block.CustomBlock
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerJumpEvent
import one.oktw.galaxy.event.type.PlayerSneakEvent

class Elevator {
    private fun isElevator(world: ServerLevel, blockPos: BlockPos): Boolean {
        return (world.getBlockEntity(blockPos) as? CustomBlockEntity)?.getId() == CustomBlock.ELEVATOR.identifier
    }

    private fun isSafe(world: ServerLevel, blockPos: BlockPos): Boolean {
        return !world.getBlockState(blockPos).isSolidRender
    }

    private fun doTeleport(player: ServerPlayer, pos: BlockPos) {
        player.teleportTo(player.x, pos.y.toDouble(), player.z)
        player.deltaMovement = Vec3.ZERO
        player.connection.send(ClientboundSetEntityMotionPacket(player))
        player.level().playSound(
            null,
            BlockPos(pos),
            SoundEvents.CHORUS_FRUIT_TELEPORT,
            SoundSource.BLOCKS,
            1.0f,
            1.0f
        )
    }

    @EventListener(sync = true)
    fun onJump(event: PlayerJumpEvent) {
        val player = event.player
        val playerWorld = player.level()
        val blockPos = player.blockPosition()

        if (isElevator(playerWorld, blockPos.below()) && isSafe(playerWorld, blockPos)) {
            for (i in 1..7) {
                val nextBlockPos = blockPos.above(i)
                if (isElevator(playerWorld, nextBlockPos) && isSafe(playerWorld, nextBlockPos.above())) {
                    doTeleport(player, nextBlockPos.above())
                    break
                }
            }
        }
    }

    @EventListener(sync = true)
    fun onSneak(event: PlayerSneakEvent) {
        val player = event.player
        val playerWorld = player.level()
        val blockPos = player.blockPosition()

        if (isElevator(playerWorld, blockPos.below()) && isSafe(playerWorld, blockPos)) {
            for (i in 3..9) {
                val nextBlockPos = blockPos.below(i)
                if (isElevator(playerWorld, nextBlockPos) && isSafe(playerWorld, nextBlockPos.above())) {
                    doTeleport(player, nextBlockPos.above())
                    break
                }
            }
        }
    }
}
