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

package one.oktw.galaxy.player

import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.Hand
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerUpdateSignEvent

class Sign {
    @EventListener(sync = true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val mainhand = event.player.getStackInHand(Hand.MAIN_HAND).isEmpty
        val offhand = event.player.getStackInHand(Hand.OFF_HAND).isEmpty
        if (event.player.isSneaking && mainhand && offhand) {
            val world = event.player.serverWorld
            val blockHitResult = event.packet.hitY
            val entity = world.getBlockEntity(blockHitResult.blockPos)
            if (entity is SignBlockEntity) {
                val signBlockEntity = entity as? SignBlockEntity ?: return
                event.player.openEditSignScreen(signBlockEntity)
            }
        }
    }

    @EventListener(sync = true)
    fun onPlayerUpdateSign(event: PlayerUpdateSignEvent) {
        val world = event.player.serverWorld
        val entity = world.getBlockEntity(event.packet.pos)
        val signBlockEntity = entity as? SignBlockEntity ?: return
        for (i in 0..3) {
            val r = Regex("(?<![\\S|\\W])&(?![\\W])")
            val line = r.replace(event.packet.text[i], "§")
            signBlockEntity.setTextOnRow(i, LiteralText(line))
        }
        event.cancel = true
        world.players.forEach {
            it.networkHandler.sendPacket(signBlockEntity.toUpdatePacket())
        }
        event.player.networkHandler.sendPacket(signBlockEntity.toUpdatePacket())
        
    }
}
