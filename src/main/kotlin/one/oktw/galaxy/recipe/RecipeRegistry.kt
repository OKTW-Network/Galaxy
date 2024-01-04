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

package one.oktw.galaxy.recipe

import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import one.oktw.galaxy.mixin.interfaces.CustomRecipeManager
import one.oktw.galaxy.recipe.blocks.Elevator
import one.oktw.galaxy.recipe.blocks.HTCraftingTable
import one.oktw.galaxy.recipe.blocks.Harvest
import one.oktw.galaxy.recipe.blocks.Trashcan
import one.oktw.galaxy.recipe.materials.CeramicPlate
import one.oktw.galaxy.recipe.tools.Crowbar
import one.oktw.galaxy.recipe.tools.Wrench

object RecipeRegistry {
    fun register() {
        // Recipe
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "item/wrench"), Wrench())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "item/crowbar"), Crowbar())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "block/elevator"), Elevator())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "block/htct"), HTCraftingTable())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "block/harvest"), Harvest())
        CustomRecipeManager.addRecipe(RecipeType.SMELTING, Identifier("galaxy", "material/ceramic_plate"), CeramicPlate())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "block/trashcan"), Trashcan())
    }
}
