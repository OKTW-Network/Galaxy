package one.oktw.galaxy.helper

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.gui.GUI
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.scheduler.Task
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

class GUIHelper {
    companion object {
        private val sync = ConcurrentHashMap<Player, ConcurrentLinkedDeque<GUI>>()

        fun open(player: Player, create: () -> GUI): GUI {
            val new =
                create().apply { registerEvent(InteractInventoryEvent.Close::class.java, this@Companion::closeEvent) }
            val gui = sync.values.mapNotNull { it.firstOrNull { it.token == new.token } }.firstOrNull() ?: new

            sync.getOrPut(player) { ConcurrentLinkedDeque() }.offerLast(gui)
            player.openInventory(gui.inventory)

            return gui
        }

        fun close(token: String) {
            sync.forEach(5) { player, stack ->
                if (stack.peekLast()?.token == token) {
                    stack.pollLast()
                    val gui = stack.peekLast()
                    if (gui != null) player.openInventory(gui.inventory) else closeInventory(player)
                }

                stack.removeIf { it.token == token }
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

        private fun closeInventory(player: Player) {
            Task.builder().execute { _ -> player.closeInventory() }.submit(main)
        }
    }
}