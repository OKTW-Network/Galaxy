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

package one.oktw.galaxy.galaxy.traveler.data

import one.oktw.galaxy.Main.Companion.dummyUUID
import one.oktw.galaxy.economy.StarDustKeeper
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.item.type.Item
import one.oktw.galaxy.item.type.Upgrade
import org.spongepowered.api.item.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

data class Traveler(
    val uuid: UUID = dummyUUID,
    var group: Group = VISITOR,
    var armor: ArrayList<Upgrade> = ArrayList(),
    var item: ArrayList<Item> = ArrayList(),
    var inventory: ArrayList<ItemStack> = ArrayList(),
    var enderChest: ArrayList<ItemStack> = ArrayList(),
    var experience: Int = 0
) : StarDustKeeper()
