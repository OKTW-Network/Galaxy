/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.recipe.easyRecipe

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.world.World
import one.oktw.galaxy.recipe.utils.Ingredient
import one.oktw.galaxy.recipe.utils.RecipeUtils

class StainedGlassRestore : CraftingRecipe {
    private val stainedGlass = listOf(
        Items.WHITE_STAINED_GLASS,
        Items.ORANGE_STAINED_GLASS,
        Items.MAGENTA_STAINED_GLASS,
        Items.LIGHT_BLUE_STAINED_GLASS,
        Items.YELLOW_STAINED_GLASS,
        Items.LIME_STAINED_GLASS,
        Items.PINK_STAINED_GLASS,
        Items.GRAY_STAINED_GLASS,
        Items.LIGHT_GRAY_STAINED_GLASS,
        Items.CYAN_STAINED_GLASS,
        Items.PURPLE_STAINED_GLASS,
        Items.BLUE_STAINED_GLASS,
        Items.BROWN_STAINED_GLASS,
        Items.GREEN_STAINED_GLASS,
        Items.RED_STAINED_GLASS,
        Items.BLACK_STAINED_GLASS
    )

    override fun matches(inv: CraftingInventory, world: World): Boolean {
        var match = false
        val stainedGlass = Ingredient(items = stainedGlass)
        val waterBucket = Ingredient(items = listOf(Items.WATER_BUCKET))

        val list = listOf(waterBucket)

        for (count in 1..8) {
            val countedList = list.toMutableList()

            for (addCount in 1..count) {
                countedList.add(stainedGlass)
            }

            if (RecipeUtils.isItemShapelessMatches(inv, countedList)) {
                match = true
                break
            }
        }

        return match
    }

    override fun craft(inv: CraftingInventory): ItemStack {
        var item = ItemStack.EMPTY
        val stainedGlass = Ingredient(items = stainedGlass)
        val waterBucket = Ingredient(items = listOf(Items.WATER_BUCKET))

        val list = listOf(waterBucket)

        for (count in 1..8) {
            val countedList = list.toMutableList()

            for (addCount in 1..count) {
                countedList.add(stainedGlass)
            }

            if (RecipeUtils.isItemShapelessMatches(inv, countedList)) {
                item = Items.GLASS.defaultStack.apply { this.count = count }
                break
            }
        }
        return item
    }

    @Environment(EnvType.CLIENT)
    override fun fits(width: Int, height: Int): Boolean {
        throw NotImplementedError()
    }

    override fun getOutput() = Items.GLASS.defaultStack.apply { this.count = 1 }

    override fun getId() = Identifier("galaxy", "easy_recipe/stained_glass_restore")

    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented, support client mod.")
    }
}
