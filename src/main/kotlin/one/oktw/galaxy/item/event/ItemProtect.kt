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

package one.oktw.galaxy.item.event

import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.item.inventory.InventoryArchetypes

class ItemProtect {
    @Listener(order = Order.FIRST)
    fun onDropItem(event: DropItemEvent.Pre, @Root player: Player) {
        event.droppedItems.any { it[DataUUID.key].isPresent }.let(event::setCancelled)
    }

    @Listener
    fun onChangeInventory(event: ClickInventoryEvent) {
        if (event.targetInventory.archetype == InventoryArchetypes.PLAYER) return

        val cursor = event.cursorTransaction.default.let {
            it[DataUUID.key].isPresent && it[DataItemType.key].orElse(null) != BUTTON
        }
        val move = event.transactions.any {
            it.default[DataUUID.key].isPresent && it.default[DataItemType.key].orElse(null) != BUTTON
        }

        if (cursor || move) event.isCancelled = true
    }
}
