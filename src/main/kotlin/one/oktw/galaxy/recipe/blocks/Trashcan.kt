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

import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.RawShapedRecipe
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import one.oktw.galaxy.item.CustomBlockItem

class Trashcan : ShapedRecipe(
    "",
    CraftingRecipeCategory.BUILDING,
    RawShapedRecipe.create(
        mapOf(
            Character.valueOf('g') to ofItems(Items.GLASS),
            Character.valueOf('c') to ofItems(Items.CACTUS),
            Character.valueOf('t') to ofItems(Items.TERRACOTTA),
            Character.valueOf('s') to ofItems(Items.SAND, Items.RED_SAND)
        ),
        "ggg",
        "gcg",
        "tst"
    ),
    CustomBlockItem.TRASHCAN.createItemStack()
)
