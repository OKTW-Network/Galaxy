/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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

package one.oktw.galaxy.item.recipe

import net.minecraft.world.item.Items
import one.oktw.galaxy.item.CustomBlockItem.Companion.HT_CRAFTING_TABLE

class HTCraftingTable : CustomItemRecipe() {
    override val ingredients = listOf(
        CustomIngredient(Items.REDSTONE, 2),
        CustomIngredient(Items.IRON_INGOT, 2),
        CustomIngredient(Items.LAPIS_LAZULI, 2),
        CustomIngredient(Items.DIAMOND, 1),
        CustomIngredient(Items.CRAFTING_TABLE, 1),
        CustomIngredient(Items.OBSIDIAN, 1)
    )
    override val outputItem = HT_CRAFTING_TABLE
}
