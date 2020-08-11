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

import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.util.CustomBlockUtil
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerJumpEvent
import one.oktw.galaxy.event.type.PlayerSneakEvent

class Elevator {
    @EventListener(sync = true)
    fun onJump(event: PlayerJumpEvent) {
        val position = event.player.pos
        if(CustomBlockUtil.positionIsBlock(event.player.serverWorld, BlockPos(position.subtract(0.0, 1.0, 0.0)), BlockType.ELEVATOR)) {
            for (i in 3..16) {
                if (CustomBlockUtil.positionIsBlock(event.player.serverWorld, BlockPos(position.add(0.0, i.toDouble(), 0.0)), BlockType.ELEVATOR)) {
                    event.player.requestTeleport(position.x, position.y+i+1, position.z)
                    event.player.world.playSound(null, BlockPos(position.add(0.0, i.toDouble(), 0.0)), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    break
                }
            }
        }
    }
    @EventListener(sync = true)
    fun onSneak(event: PlayerSneakEvent) {
        val position = event.player.pos
        if(CustomBlockUtil.positionIsBlock(event.player.serverWorld, BlockPos(position.subtract(0.0, 1.0, 0.0)), BlockType.ELEVATOR)) {
            for (i in 3..16) {
                if (CustomBlockUtil.positionIsBlock(event.player.serverWorld, BlockPos(position.subtract(0.0, i.toDouble(), 0.0)), BlockType.ELEVATOR)) {
                    event.player.requestTeleport(position.x, position.y-i+1, position.z)
                    event.player.world.playSound(null, BlockPos(position.subtract(0.0, i.toDouble(), 0.0)), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    break
                }
            }
        }
    }
}
