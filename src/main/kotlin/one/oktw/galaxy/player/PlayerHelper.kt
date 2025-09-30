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

package one.oktw.galaxy.player

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

object PlayerHelper {
    fun giveItemToPlayer(player: ServerPlayerEntity, itemStack: ItemStack) {
        if (player.inventory.insertStack(itemStack)) {
            itemStack.count = 1
            val itemEntity = player.dropItem(itemStack, false)
            itemEntity?.setDespawnImmediately()

            player.world.playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS,
                0.2F,
                ((player.random.nextFloat() - player.random.nextFloat()) * 0.7F + 1.0F) * 2.0F
            )
            player.playerScreenHandler.sendContentUpdates()
        } else {
            player.dropItem(itemStack, false)?.apply {
                resetPickupDelay()
                setOwner(player.uuid)
            }
        }
    }
}
