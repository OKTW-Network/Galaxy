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

package one.oktw.galaxy.recipe.materials

import net.minecraft.inventory.Inventory
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.SmeltingRecipe
import net.minecraft.recipe.book.CookingRecipeCategory
import net.minecraft.world.World
import one.oktw.galaxy.item.CustomItemHelper
import one.oktw.galaxy.item.Material

class CeramicPlate : SmeltingRecipe(
    "",
    CookingRecipeCategory.MISC,
    Ingredient.ofStacks(Material.RAW_BASE_PLATE.createItemStack()),
    Material.BASE_PLATE.createItemStack(),
    0.1F,
    200
) {
    override fun matches(inventory: Inventory, world: World): Boolean {
        val input = inventory.getStack(0) ?: return false
        return CustomItemHelper.getItem(input) == Material.RAW_BASE_PLATE
    }
}
