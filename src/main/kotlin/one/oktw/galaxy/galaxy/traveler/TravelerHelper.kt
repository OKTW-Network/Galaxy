/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.galaxy.traveler

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.armor.ArmorHelper.offerArmor
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.data.key.Keys.EXPERIENCE_LEVEL
import org.spongepowered.api.data.key.Keys.TOTAL_EXPERIENCE
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack.empty
import org.spongepowered.api.item.inventory.Slot

class TravelerHelper {
    companion object {
        suspend fun getTraveler(player: Player) = galaxyManager.get(player.world)?.getMember(player.uniqueId)

        fun saveTraveler(traveler: Traveler, player: Player): Traveler? {
            traveler.experience = player[TOTAL_EXPERIENCE].get()
            traveler.inventory = player.inventory.slots<Slot>().mapTo(ArrayList()) { it.peek().orElse(empty()) }
            traveler.enderChest = player.enderChestInventory.slots<Slot>().mapTo(ArrayList()) { it.peek().orElse(empty()) }

            if (traveler.experience == 0 && traveler.inventory.all { it == empty() }) {
                main.logger.error("Try save empty player: ", player.name.toString(), RuntimeException("Saving empty player"))
                return null
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
