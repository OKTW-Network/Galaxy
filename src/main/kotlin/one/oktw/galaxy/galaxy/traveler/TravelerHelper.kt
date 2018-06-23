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
            val traveler = getTraveler(player).await()?.apply {
                experience = player[Keys.TOTAL_EXPERIENCE].get()
                inventory = player.inventory.slots<Slot>().map { it.peek().orElse(ItemStack.empty()) }
            } ?: return@async null

            if (clean) cleanPlayer(player)

            galaxyManager.get(player.world).await()?.members?.run {
                set(indexOfFirst { it.uuid == player.uniqueId }, traveler)
            }

            return@async traveler
        }

        fun loadTraveler(traveler: Traveler, player: Player) {
            player.offer(Keys.TOTAL_EXPERIENCE, traveler.experience)
            player.inventory.slots<Slot>().forEachIndexed { index, slot -> slot.set(traveler.inventory[index]) }
        }

        fun cleanPlayer(player: Player) {
            player.offer(Keys.TOTAL_EXPERIENCE, 0)
            player.inventory.clear()
        }
    }
}
