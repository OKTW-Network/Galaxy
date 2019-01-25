/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

package one.oktw.galaxy.armor.event

import one.oktw.galaxy.armor.ArmorEffect.Companion.removeAllEffect
import one.oktw.galaxy.armor.ArmorHelper.offerArmor
import one.oktw.galaxy.armor.ArmorHelper.toggleBoots
import one.oktw.galaxy.armor.ArmorHelper.toggleChestplate
import one.oktw.galaxy.armor.ArmorHelper.toggleHelmet
import one.oktw.galaxy.armor.ArmorHelper.toggleLeggings
import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.enums.ItemType.ARMOR
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.item.ItemTypes.*
import org.spongepowered.api.item.inventory.ItemStackSnapshot

class Armor {
    @Listener
    fun onPlayerDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        removeAllEffect(player)
    }

    @Listener
    fun onClickInventory(event: ClickInventoryEvent, @First player: Player) {
        val item = event.cursorTransaction.default.createStack()

        if (item[DataItemType.key].orElse(null) != ARMOR) {
            if (event.transactions.any { it.default[DataItemType.key].orElse(null) == ARMOR }) event.isCancelled = true
            return
        }

        if (item[DataEnable.key].isPresent) {
            event.cursorTransaction.apply {
                setCustom(ItemStackSnapshot.NONE)
                isValid = true
            }

            when (item.type) {
                DIAMOND_HELMET -> toggleHelmet(player)
                DIAMOND_CHESTPLATE -> toggleChestplate(player)
                DIAMOND_LEGGINGS -> toggleLeggings(player)
                DIAMOND_BOOTS -> toggleBoots(player)
            }
        } else {
            event.isCancelled = true
        }
    }

    @Listener
    fun onRespawn(event: RespawnPlayerEvent, @Getter("getTargetEntity") player: Player) {
        if (event.isDeath) offerArmor(player)
    }
}
