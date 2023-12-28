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
import one.oktw.galaxy.recipe.easyRecipe.*
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
        // Easy Recipe
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/ladder"), Ladder())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/chest"), Chest())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/hopper"), Hopper())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/redstone_lamp"), RedStoneLamp())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/dispenser"), Dispenser())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/dispenser_with_bow"), DispenserWithBow())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/redstone_repeater"), RedStoneRepeater())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/trapped_chest"), TrappedChest())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/carrot_on_a_stick"), CarrotOnAStick())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/warped_fungus_on_a_stick"), WarpedFungusOnAStick())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/writable_book"), BookAndQuill())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/minecart"), Minecart())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/stick"), Stick())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/glass"), Glass())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/glass_pane"), GlassPane())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/glass_pane_restore"), GlassPaneRestore())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/stained_glass_restore"), StainedGlassRestore())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/stained_glass_pane_restore"), StainedGlassPaneRestore())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/wood_slab"), WoodenSlab())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Identifier("galaxy", "easy_recipe/slab_restore"), SlabRestore())
    }
}
