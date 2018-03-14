package one.oktw.galaxy.gui

import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory

typealias Listener = (InteractInventoryEvent) -> Unit
typealias EventClass = Class<out InteractInventoryEvent>

abstract class GUI {
    abstract val token: String
    abstract val inventory: Inventory
    private val eventListeners = ArrayList<Pair<EventClass, Listener>>()

    fun <T : InteractInventoryEvent> registerEvent(event: EventClass, listener: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST") // TODO better code
        eventListeners.add(Pair(event, listener) as Pair<EventClass, Listener>)
    }

    protected fun eventProcess(event: InteractInventoryEvent) {
        eventListeners.forEach { if (it.first.isInstance(event)) it.second(it.first.cast(event)) }
    }
}
