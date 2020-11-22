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

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tag.Tag
import one.oktw.galaxy.recipe.tags.CustomTag
import java.security.InvalidParameterException

class Ingredient(
    private val item: Item? = null,
    private val tag: Tag.Identified<Item>? = null,
    private val itemStack: ItemStack? = null,
    private val customTag: CustomTag? = null
) {
    init {
        if (item == null && tag == null && itemStack == null && customTag == null) throw InvalidParameterException("No input provided.")
        if (
            (item != null && tag != null) ||
            (item != null && itemStack != null) ||
            (item != null &&customTag != null) ||
            (tag != null && itemStack != null) ||
            (tag != null && customTag != null) ||
            (itemStack != null && customTag != null)
        ) throw InvalidParameterException("Only one input is allowed.")
    }

    fun matches(input: ItemStack): Boolean {
        return when {
            item != null -> input.item == item
            tag != null -> tag.contains(input.item)
            itemStack != null -> itemStack.isItemEqual(input)
            customTag != null -> customTag.contains(input.item)
            else -> false
        }
    }
}
