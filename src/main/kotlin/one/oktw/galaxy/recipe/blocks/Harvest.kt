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

package one.oktw.galaxy.recipe.blocks

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier
import net.minecraft.world.World
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.recipe.utils.Ingredient
import one.oktw.galaxy.recipe.utils.RecipeUtils

class Harvest : CraftingRecipe {
    private val item = CustomBlockItem.HARVEST.createItemStack()

    private val copper = Ingredient(items = listOf(Items.COPPER_INGOT))
    private val observer = Ingredient(items = listOf(Items.OBSERVER))
    private val dispenser = Ingredient(items = listOf(Items.DISPENSER))
    private val list = listOf(
        copper, copper, copper,
        copper, dispenser, observer,
        copper, copper, copper
    )

    override fun matches(inv: RecipeInputInventory, world: World): Boolean = RecipeUtils.isItemShapedMatches(inv, 3, 3, list)

    override fun craft(inventory: RecipeInputInventory, registryManager: DynamicRegistryManager): ItemStack = item.copy()

    @Environment(EnvType.CLIENT)
    override fun fits(width: Int, height: Int): Boolean {
        throw NotImplementedError()
    }

    override fun getOutput(registryManager: DynamicRegistryManager) = item

    override fun getId() = Identifier("galaxy", "block/harvest")

    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not implemented client mod")
    }

    override fun getCategory() = CraftingRecipeCategory.BUILDING
}
