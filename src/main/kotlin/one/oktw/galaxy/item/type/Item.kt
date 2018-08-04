package one.oktw.galaxy.item.type

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.enums.ItemType
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe
import org.spongepowered.api.item.recipe.smelting.SmeltingResult
import java.util.*


@BsonDiscriminator
interface Item : Ingredient {
    companion object {
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

            override fun getResult(ingredient: ItemStackSnapshot?): Optional<SmeltingResult> {
                return Optional.of(SmeltingResult(result, experience))
            }

            override fun isValid(toTest: ItemStackSnapshot?): Boolean {
                val stack = toTest?.createStack() ?: return false
                main.logger.info(stack[DataItemType.key].toString())
                main.logger.info(stack[Keys.ITEM_DURABILITY].toString())

                return ingredient.test(stack)
            }

        }

        fun Item.createSmeltingRecipe(result: ItemStackSnapshot, experience: Double): SmeltingRecipe {
            return CustomSmeltingRecipe(this, result, experience)
        }
    }

    val itemType: ItemType

    fun createItemStack(): ItemStack
}

