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
import net.minecraft.network.MessageType
import net.minecraft.text.LiteralText
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent


class Sign {

    @EventListener(sync = true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val world = event.player.serverWorld
        val blockHitResult = event.packet.hitY
        val entity = world.getBlockEntity(blockHitResult.blockPos)
        if (event.player.isSneaking) {
            if (entity is SignBlockEntity) {
                val signBlockEntity = entity as SignBlockEntity?
                if (signBlockEntity != null) {
                    event.player.sendMessage(LiteralText("successful open the editor"))
                    event.player.openEditSignScreen(signBlockEntity)
                }
            }
        }


    }

}
