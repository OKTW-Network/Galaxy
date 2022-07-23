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
import one.oktw.galaxy.item.CustomItem

class HTCraftingBuilder {
    private val recipe = HTCraftingRecipe()

    fun add(item: Ingredient, count: Int): HTCraftingBuilder {
        recipe.add(item, count)
        return this
    }

    fun cost(price: Int): HTCraftingBuilder {
        recipe.price(price)
        return this
    }

    fun result(newResult: Item): HTCraftingBuilder {
        recipe.setResult(newResult)
        return this
    }

    fun result(newResult: ItemStack): HTCraftingBuilder {
        recipe.setResult(newResult)
        return this
    }

    fun customItemResult(newResult: CustomItem): HTCraftingBuilder {
        recipe.setCustomResult(newResult)
        return this
    }

    fun build(): HTCraftingRecipe {
        return recipe
    }

    fun builder() = HTCraftingBuilder()
}
