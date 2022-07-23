/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

package one.oktw.galaxy.item

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object CustomItemHelper {
    fun getItem(itemStack: ItemStack): CustomItem? {
        if (itemStack.count == 0) return null
        val customNbt = itemStack.getSubNbt("GalaxyData")

        return customNbt?.getString("CustomItemIdentifier")?.let(Identifier::tryParse)
            ?.let(CustomItem.registry::get)
            ?.run { readCustomNbt(customNbt) }
    }
}
