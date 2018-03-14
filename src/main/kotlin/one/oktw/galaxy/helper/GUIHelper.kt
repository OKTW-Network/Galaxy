package one.oktw.galaxy.helper

import one.oktw.galaxy.Main
import one.oktw.galaxy.gui.GUI
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GUIHelper {
    companion object {
        private val sync = ConcurrentHashMap<UUID, GUI>()

        fun open(player: Player, create: () -> GUI): GUI {
            val new = create()
            val gui = sync.values.firstOrNull { it.getToken() == new.getToken() } ?: new

            sync[player.uniqueId] = gui
            player.openInventory(gui.inventory)

            return gui
        }

        fun closeAll(token: String) {
            sync.filterValues { it.getToken() == token }.keys.forEach {
                Sponge.getServer().getPlayer(it).ifPresent {
                    Task.builder().execute { _ -> it.closeInventory() }.submit(
                        Main.main
                    )
                }
            }
        }
    }
}