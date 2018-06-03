package one.oktw.galaxy.item.event

import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.item.inventory.InventoryArchetypes

class ItemProtect {
    @Listener(order = Order.FIRST)
    fun onDropItem(event: DropItemEvent.Pre) {
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
