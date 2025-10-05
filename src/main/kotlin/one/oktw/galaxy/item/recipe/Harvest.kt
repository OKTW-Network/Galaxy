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

import net.minecraft.item.Items
import one.oktw.galaxy.item.CustomBlockItem.Companion.HARVEST

class Harvest : CustomItemRecipe() {
    override val ingredients = listOf(
        CustomIngredient(Items.COPPER_INGOT, 7),
        CustomIngredient(Items.DISPENSER, 1),
        CustomIngredient(Items.OBSERVER, 1)
    )
    override val outputItem = HARVEST
}
