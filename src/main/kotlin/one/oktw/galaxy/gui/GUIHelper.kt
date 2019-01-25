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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.item.enums.ButtonType.GUI_CENTER
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

class GUIHelper {
    companion object {
        private val sync = ConcurrentHashMap<Player, ConcurrentLinkedDeque<GUI>>()

        fun open(player: Player, create: () -> GUI): GUI {
            val new = create().apply { registerEvent(InteractInventoryEvent.Close::class.java, ::closeEvent) }
            val gui = sync.values.mapNotNull { it.firstOrNull { it.token == new.token } }.firstOrNull() ?: new

            sync.getOrPut(player) { ConcurrentLinkedDeque() }.offerLast(gui)
            GlobalScope.launch(serverThread) { player.openInventory(gui.inventory) }

            return gui
        }

        fun openAsync(player: Player, create: suspend () -> GUI) = GlobalScope.async {
            val new = create().apply { registerEvent(InteractInventoryEvent.Close::class.java, ::closeEvent) }
            val gui = sync.values.mapNotNull { it.firstOrNull { it.token == new.token } }.firstOrNull() ?: new

            sync.getOrPut(player) { ConcurrentLinkedDeque() }.offerLast(gui)
            launch(serverThread) { player.openInventory(gui.inventory) }

            return@async gui
        }

        fun close(token: String) {
            sync.forEach(5) { player, stack ->
                if (stack.peekLast()?.token == token) {
                    stack.pollLast()
                    val gui = stack.peekLast()
                    GlobalScope.launch(serverThread) { gui?.run { player.openInventory(inventory) } ?: player.closeInventory() }
                }

                stack.removeIf { it.token == token }
            }
        }

        fun closeAll(player: Player) {
            GlobalScope.launch(serverThread) { player.closeInventory() }
            sync.filterKeys { it.uniqueId == player.uniqueId }.keys.forEach { sync -= it }
            GlobalScope.launch { cleanOffline() }
        }

        fun fillEmptySlot(inventory: Inventory) {
            val item = Button(GUI_CENTER).createItemStack()

            while (inventory.size() < inventory.capacity()) {
                if (inventory.offer(item.copy()).type != InventoryTransactionResult.Type.SUCCESS) break
            }
        }

        private fun closeEvent(event: InteractInventoryEvent.Close) {
            val player = event.source as? Player ?: return
            val stack = sync[player] ?: return

            // Remove closed
            stack.pollLast()

            val gui = stack.peekLast()

            if (gui != null) {
                player.openInventory(gui.inventory)
            } else {
                sync -= player
            }
        }

        private fun cleanOffline() {
            sync.filterKeys { !it.isOnline }.keys.forEach { sync -= it }
        }
    }
}
