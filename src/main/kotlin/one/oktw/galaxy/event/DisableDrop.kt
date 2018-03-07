package one.oktw.galaxy.event

import one.oktw.galaxy.data.DataUUID
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.item.inventory.DropItemEvent

class DisableDrop {
    @Listener(order = Order.FIRST)
    fun onDropItem(event: DropItemEvent.Pre, @First player: Player) {
        if (event.droppedItems.any { it[DataUUID.key].isPresent })
            event.isCancelled = true
    }
}