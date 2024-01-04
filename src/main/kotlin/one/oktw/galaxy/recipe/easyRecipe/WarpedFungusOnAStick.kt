/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.Items
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.world.World
import one.oktw.galaxy.recipe.utils.Ingredient
import one.oktw.galaxy.recipe.utils.RecipeUtils

class WarpedFungusOnAStick : CraftingRecipe {
    private val item = Items.WARPED_FUNGUS_ON_A_STICK.defaultStack

    private val air = Ingredient(items = listOf(Items.AIR))
    private val stick = Ingredient(items = listOf(Items.STICK))
    private val strings = Ingredient(items = listOf(Items.STRING))
    private val warpedFungus = Ingredient(items = listOf(Items.WARPED_FUNGUS))
    private val listLeft = listOf(
        stick, air, air,
        strings, stick, air,
        strings, warpedFungus, stick
    )
    private val listRight = listOf(
        air, air, stick,
        air, stick, strings,
        stick, warpedFungus, strings
    )

    override fun matches(inv: RecipeInputInventory, world: World): Boolean =
        (RecipeUtils.isItemShapedMatches(inv, 3, 3, listLeft) || RecipeUtils.isItemShapedMatches(inv, 3, 3, listRight))

    override fun craft(inv: RecipeInputInventory, registryManager: DynamicRegistryManager) = item.copy()

    @Environment(EnvType.CLIENT)
    override fun fits(width: Int, height: Int): Boolean {
        throw NotImplementedError()
    }

    override fun getResult(registryManager: DynamicRegistryManager) = item


    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented, support client mod.")
    }

    override fun getCategory() = CraftingRecipeCategory.BUILDING
}
