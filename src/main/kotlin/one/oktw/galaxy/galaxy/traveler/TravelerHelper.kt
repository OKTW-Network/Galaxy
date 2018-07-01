package one.oktw.galaxy.galaxy.traveler

import kotlinx.coroutines.experimental.async
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.armor.ArmorHelper.Companion.offerArmor
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack.empty
import org.spongepowered.api.item.inventory.Slot
import java.util.*

class TravelerHelper {
    companion object {
        fun getTraveler(player: Player) = async { galaxyManager.get(player.world).await()?.getMember(player.uniqueId) }

        fun saveTraveler(traveler: Traveler, player: Player): Traveler {
            traveler.experience = player[Keys.TOTAL_EXPERIENCE].get()
            traveler.inventory = player.inventory.slots<Slot>().mapTo(ArrayList()) { it.peek().orElse(empty()) }

            return traveler
        }

        fun loadTraveler(traveler: Traveler, player: Player) {
            // xp
            player.offer(Keys.TOTAL_EXPERIENCE, traveler.experience)

            // inventory
            player.inventory.slots<Slot>().forEachIndexed { index, slot ->
                slot.set(traveler.inventory.getOrElse(index) { empty() })
            }

            // armor
            offerArmor(player)
        }

        fun cleanPlayer(player: Player) {
            player.offer(Keys.TOTAL_EXPERIENCE, 0)
            player.inventory.clear()
        }
    }
}
