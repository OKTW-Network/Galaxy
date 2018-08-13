package one.oktw.galaxy.gui

import com.google.common.collect.MapMaker
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import java.lang.ref.SoftReference

abstract class GUI {
    private val eventListener = ArrayList<(InteractInventoryEvent) -> Unit>()
    abstract val token: String
    abstract val inventory: Inventory

    // Unsafe workaround for sponge can't unregister inventory listener
    // revert if sponge fix this issue
    fun <T : InteractInventoryEvent> registerEvent(event: Class<T>, listener: (T) -> Unit) = registerEvent(this, event, listener)

    companion object {
        private val listeners = MapMaker()
            .weakKeys()
            .makeMap<GUI, ArrayList<Pair<Class<out InteractInventoryEvent>, SoftReference<(InteractInventoryEvent) -> Unit>>>>()

        fun <T : InteractInventoryEvent> registerEvent(gui: GUI, event: Class<T>, listener: (T) -> Unit) {
            @Suppress("UNCHECKED_CAST")
            gui.eventListener += listener as (InteractInventoryEvent) -> Unit

            @Suppress("UNCHECKED_CAST")
            listeners.getOrPut(gui) { ArrayList() }
                .add(Pair(event, SoftReference(listener)) as Pair<Class<out InteractInventoryEvent>, SoftReference<(InteractInventoryEvent) -> Unit>>)
        }

        fun eventProcess(event: InteractInventoryEvent) {
            listeners.filterKeys { event.targetInventory.containsInventory(it.inventory) }.values.firstOrNull()?.forEach {
                if (it.first.isInstance(event)) it.second.get()!!.invoke(it.first.cast(event))
            }
        }
    }
}
