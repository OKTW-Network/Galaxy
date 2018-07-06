package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.enums.ButtonType.GUI_CENTER
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.data.key.Keys.DISPLAY_NAME
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult
import org.spongepowered.api.text.Text
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

class GUIHelper {
    companion object {
        private val sync = ConcurrentHashMap<Player, ConcurrentLinkedDeque<GUI>>()

        fun open(player: Player, create: () -> GUI): GUI {
            val new = create().apply { registerEvent(InteractInventoryEvent.Close::class.java, ::closeEvent) }
            val gui = sync.values.mapNotNull { it.firstOrNull { it.token == new.token } }.firstOrNull() ?: new

            sync.getOrPut(player) { ConcurrentLinkedDeque() }.offerLast(gui)
            launch(serverThread) { player.openInventory(gui.inventory) }

            return gui
        }

        fun openAsync(player: Player, create: suspend () -> GUI) = async {
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
                    launch(serverThread) { gui?.run { player.openInventory(inventory) } ?: player.closeInventory() }
                }

                stack.removeIf { it.token == token }
            }
        }

        fun closeAll(player: Player) {
            launch(serverThread) { player.closeInventory() }
            sync -= player
        }

        fun fillEmptySlot(inventory: Inventory) {
            val item = Button(GUI_CENTER).createItemStack().apply {
                offer(DISPLAY_NAME, Text.of(""))
                offer(DataItemType(BUTTON))
            }

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
    }
}
