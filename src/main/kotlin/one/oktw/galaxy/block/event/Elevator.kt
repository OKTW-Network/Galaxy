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

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import one.oktw.galaxy.block.CustomBlock
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerJumpEvent
import one.oktw.galaxy.event.type.PlayerSneakEvent

class Elevator {
    private fun isElevator(world: ServerWorld, blockPos: BlockPos): Boolean {
        return (world.getBlockEntity(blockPos) as? CustomBlockEntity)?.getId() == CustomBlock.ELEVATOR.identifier
    }

    private fun isSafe(world: ServerWorld, blockPos: BlockPos): Boolean {
        return !world.getBlockState(blockPos).isOpaqueFullCube
    }

    private fun doTeleport(player: ServerPlayerEntity, pos: BlockPos) {
        player.requestTeleport(player.pos.x, pos.y.toDouble(), player.pos.z)
        player.velocity = Vec3d.ZERO
        player.networkHandler.sendPacket(EntityVelocityUpdateS2CPacket(player))
        player.entityWorld.playSound(
            null,
            BlockPos(pos),
            SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
            SoundCategory.BLOCKS,
            1.0f,
            1.0f
        )
    }

    @EventListener(sync = true)
    fun onJump(event: PlayerJumpEvent) {
        val player = event.player
        val playerWorld = player.entityWorld
        val blockPos = BlockPos.ofFloored(player.pos)

        if (isElevator(playerWorld, blockPos.down()) && isSafe(playerWorld, blockPos)) {
            for (i in 1..7) {
                val nextBlockPos = blockPos.up(i)
                if (isElevator(playerWorld, nextBlockPos) && isSafe(playerWorld, nextBlockPos.up())) {
                    doTeleport(player, nextBlockPos.up())
                    break
                }
            }
        }
    }

    @EventListener(sync = true)
    fun onSneak(event: PlayerSneakEvent) {
        val player = event.player
        val playerWorld = player.entityWorld
        val blockPos = BlockPos.ofFloored(player.pos)

        if (isElevator(playerWorld, blockPos.down()) && isSafe(playerWorld, blockPos)) {
            for (i in 3..9) {
                val nextBlockPos = blockPos.down(i)
                if (isElevator(playerWorld, nextBlockPos) && isSafe(playerWorld, nextBlockPos.up())) {
                    doTeleport(player, nextBlockPos.up())
                    break
                }
            }
        }
    }
}
