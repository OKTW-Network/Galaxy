package one.oktw.galaxy.item.type.extensions

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
