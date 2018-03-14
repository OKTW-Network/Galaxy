package one.oktw.galaxy.gui

import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory

typealias Listener = (InteractInventoryEvent) -> Unit
typealias EventClass = Class<out InteractInventoryEvent>

abstract class GUI {
    abstract val inventory: Inventory
    protected val eventListeners = ArrayList<Pair<EventClass, Listener>>()

    abstract fun getToken(): String

    fun registerEvent(event: EventClass, listener: Listener) {
        eventListeners.add(Pair(event, listener))
    }

    protected fun eventProcess(event: InteractInventoryEvent) {
        eventListeners.forEach { if (it.first.isInstance(event)) it.second(event) }
    }
}
