package one.oktw.galaxy.galaxy.traveler

import kotlinx.coroutines.experimental.async
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.Slot

class TravelerHelper {
    companion object {
        fun getTraveler(player: Player) = async { galaxyManager.get(player.world).await()?.getMember(player.uniqueId) }

        fun saveTraveler(player: Player, clean: Boolean = false) = async(serverThread) {
            getTraveler(player).await()?.let {
                it.experience = player[Keys.TOTAL_EXPERIENCE].get()
                it.inventory = player.inventory.slots<Slot>().map { it.peek().orElse(ItemStack.empty()) }
            }

            if (clean) {
                player.offer(Keys.TOTAL_EXPERIENCE, 0)
                player.inventory.clear()
            }
        }

        fun loadTraveler(traveler: Traveler, player: Player) {
            player.offer(Keys.TOTAL_EXPERIENCE, traveler.experience)
            player.inventory.slots<Slot>().forEachIndexed { index, slot -> traveler.inventory[index] }
        }
    }
}
