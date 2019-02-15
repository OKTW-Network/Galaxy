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

package one.oktw.galaxy.gui

import com.google.common.collect.MapMaker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import one.oktw.galaxy.Main.Companion.serverThread
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import java.lang.ref.SoftReference

abstract class GUI : CoroutineScope {
    override val coroutineContext by lazy { Job() + serverThread }
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
