/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

package one.oktw.galaxy.recipe.utils

import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item

object RecipeUtils {
    fun isItemFull3x3ShapedMatches(inv: CraftingInventory, list: List<Item>): Boolean {
        for (x in 0 until inv.width) {
            for (y in 0 until inv.height) {
                val index = x + y * inv.width
                if (inv.getStack(index).item != list[index]) return false
            }
        }

        return true
    }

    fun isItemShapelessMatches(inv: CraftingInventory, list: List<Item>): Boolean {
        list.forEach { item ->
            var match = false
            for (i in 0 until inv.size()) {
                val invItem = inv.getStack(i)
                if (invItem.isEmpty) continue
                if (invItem.item == item) {
                    match = true
                } else if (!list.contains(invItem.item)) {
                    return false
                }
            }
            if (!match) return false
        }
        return true
    }
}
