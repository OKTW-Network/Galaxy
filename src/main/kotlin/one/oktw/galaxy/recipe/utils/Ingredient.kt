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

package one.oktw.galaxy.recipe.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tag.TagKey

class Ingredient(
    private val tag: TagKey<Item>? = null,
    private val items: List<Item>? = null
) {
    init {
        if (tag == null && items == null) throw IllegalArgumentException("No input provided.")
        if (tag != null && items != null) throw IllegalArgumentException("Only one input is allowed.")
    }

    fun matches(input: ItemStack): Boolean {
        return when {
            tag != null -> input.isIn(tag)
            items != null -> items.contains(input.item)
            else -> false
        }
    }
}
