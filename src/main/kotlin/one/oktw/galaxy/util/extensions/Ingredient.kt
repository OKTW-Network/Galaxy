/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

package one.oktw.galaxy.util.extensions

import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe
import org.spongepowered.api.item.recipe.smelting.SmeltingResult
import java.util.*

private class CustomSmeltingRecipe(
    private val ingredient: Ingredient,
    private val result: ItemStackSnapshot,
    private val experience: Double
) : SmeltingRecipe {
    override fun getExemplaryIngredient(): ItemStackSnapshot {
        return ingredient.displayedItems()[0]
    }

    override fun getExemplaryResult(): ItemStackSnapshot {
        return result
    }

    override fun getResult(toTest: ItemStackSnapshot?): Optional<SmeltingResult> {
        val stack = toTest?.createStack() ?: return Optional.empty()

        if (!ingredient.test(stack)) {
            return Optional.empty()
        }

        return Optional.of(SmeltingResult(result, experience))
    }

    override fun isValid(toTest: ItemStackSnapshot?): Boolean {
        val stack = toTest?.createStack() ?: return false

        return ingredient.test(stack)
    }

}

fun Ingredient.createSmeltingRecipe(result: ItemStackSnapshot, experience: Double): SmeltingRecipe {
    return CustomSmeltingRecipe(this, result, experience)
}
