package one.oktw.galaxy.gui

import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory

abstract class GUI {
    abstract val token: String
    abstract val inventory: Inventory
    private val eventListeners = ArrayList<Pair<Class<out InteractInventoryEvent>, (InteractInventoryEvent) -> Unit>>()

    fun <T : InteractInventoryEvent> registerEvent(event: Class<T>, listener: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        eventListeners.add(Pair(event, listener) as Pair<Class<T>, (InteractInventoryEvent) -> Unit>)
    }

    protected fun eventProcess(event: InteractInventoryEvent) {
        eventListeners.forEach { if (it.first.isInstance(event)) it.second(it.first.cast(event)) }
    }
}
