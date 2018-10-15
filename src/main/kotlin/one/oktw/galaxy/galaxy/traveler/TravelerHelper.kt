package one.oktw.galaxy.galaxy.traveler

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.armor.ArmorHelper.Companion.offerArmor
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.data.key.Keys.EXPERIENCE_LEVEL
import org.spongepowered.api.data.key.Keys.TOTAL_EXPERIENCE
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack.empty
import org.spongepowered.api.item.inventory.Slot

class TravelerHelper {
    companion object {
        fun getTraveler(player: Player) = GlobalScope.async(Dispatchers.Default) { galaxyManager.get(player.world)?.getMember(player.uniqueId) }

        fun saveTraveler(traveler: Traveler, player: Player): Traveler {
            traveler.experience = player[TOTAL_EXPERIENCE].get()
            traveler.inventory = player.inventory.slots<Slot>().mapTo(ArrayList()) { it.peek().orElse(empty()) }
            traveler.enderChest = player.enderChestInventory.slots<Slot>().mapTo(ArrayList()) { it.peek().orElse(empty()) }

            if (traveler.experience == 0 && traveler.inventory.all { it == empty() }) {
                main.logger.error("Try save empty player!", player.toString())
                throw RuntimeException("Saving empty player")
            }

            return traveler
        }

        fun loadTraveler(traveler: Traveler, player: Player) {
            // xp
            player.offer(TOTAL_EXPERIENCE, traveler.experience)

            // inventory
            player.inventory.slots<Slot>().forEachIndexed { index, slot ->
                slot.set(traveler.inventory.getOrElse(index) { empty() })
            }

            // ender chest
            player.enderChestInventory.slots<Slot>().forEachIndexed { index, slot ->
                slot.set(traveler.enderChest.getOrElse(index) { empty() })
            }

            // armor
            offerArmor(player)
        }

        fun cleanPlayer(player: Player) {
            player.offer(EXPERIENCE_LEVEL, 0)
            player.inventory.clear()
            player.enderChestInventory.clear()
        }
    }
}
