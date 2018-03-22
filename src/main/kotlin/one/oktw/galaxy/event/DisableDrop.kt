package one.oktw.galaxy.event

import one.oktw.galaxy.data.DataUUID
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.item.inventory.InventoryArchetypes

class DisableDrop {
    @Listener(order = Order.FIRST)
    fun onDropItem(event: DropItemEvent.Pre) {
        event.droppedItems.any { it[DataUUID.key].isPresent }.let(event::setCancelled)
    }

    @Listener
    fun onChangeInventory(event: ClickInventoryEvent) {
        if (event.targetInventory.archetype == InventoryArchetypes.PLAYER) return

        if (event.cursorTransaction.default[DataUUID.key].isPresent || event.transactions.any { it.default[DataUUID.key].isPresent }) {
            event.isCancelled = true
        }
    }
}
