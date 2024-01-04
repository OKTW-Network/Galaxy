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

package one.oktw.galaxy.recipe.tools

import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RawShapedRecipe
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.DynamicRegistryManager
import one.oktw.galaxy.item.Tool

class Wrench : ShapedRecipe(
    "",
    CraftingRecipeCategory.EQUIPMENT,
    RawShapedRecipe.create(
        mapOf(Character.valueOf('i') to Ingredient.ofItems(Items.IRON_INGOT), Character.valueOf('s') to Ingredient.ofItems(Items.STICK)),
        "i i",
        " s ",
        " i "
    ),
    Tool.WRENCH.createItemStack()
) {
    override fun craft(inv: RecipeInputInventory, registryManager: DynamicRegistryManager) = Tool.WRENCH.createItemStack()
}
