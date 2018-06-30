package one.oktw.galaxy.galaxy.traveler

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.Slot
import java.util.*

class TravelerHelper {
    companion object {
        fun getTraveler(player: Player) = async { galaxyManager.get(player.world).await()?.getMember(player.uniqueId) }

        fun saveTraveler(traveler: Traveler, player: Player) = async(serverThread) {
            traveler.experience = player[Keys.TOTAL_EXPERIENCE].get()
            traveler.inventory =
                    player.inventory.slots<Slot>().mapTo(ArrayList()) { it.peek().orElse(ItemStack.empty()) }

            return@async traveler
        }

        fun loadTraveler(traveler: Traveler, player: Player) = launch(serverThread) {
            player.offer(Keys.TOTAL_EXPERIENCE, traveler.experience)
            player.inventory.slots<Slot>().forEachIndexed { index, slot ->
                slot.set(traveler.inventory.getOrElse(index) { ItemStack.empty() })
            }
        }

        fun cleanPlayer(player: Player) = launch(serverThread) {
            player.offer(Keys.TOTAL_EXPERIENCE, 0)
            player.inventory.clear()
        }
    }
}
