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
import net.minecraft.util.registry.Registry
import one.oktw.galaxy.item.CustomItem
import one.oktw.galaxy.item.CustomItemHelper

class Ingredient(
    private val previewItem: ItemStack? = null,
    private val tag: TagKey<Item>? = null,
    private val items: List<Item>? = null,
    private val customItem: List<CustomItem>? = null
) {
    init {
        if (tag == null && items == null && customItem == null) throw IllegalArgumentException("No input provided.")
        if (tag != null && items != null || customItem != null && items != null || tag != null && customItem != null) {
            throw IllegalArgumentException("Only one input is allowed.")
        }
    }

    fun matches(input: ItemStack): Boolean {
        return when {
            tag != null -> input.isIn(tag)
            items != null -> items.contains(input.item)
            customItem != null -> customItem.contains(CustomItemHelper.getItem(input))
            else -> false
        }
    }

    fun genPreviewItem(): ItemStack {
        return when {
            previewItem != null -> previewItem
            tag != null -> ItemStack(Registry.ITEM.iterateEntries(tag).first())
            items != null -> items.first().defaultStack
            customItem != null -> customItem.first().createItemStack()
            else -> throw IllegalArgumentException("No input provided.")
        }
    }
}
