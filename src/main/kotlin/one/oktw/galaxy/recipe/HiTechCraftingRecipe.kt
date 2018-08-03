package one.oktw.galaxy.recipe

import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.Slot
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors


class HiTechCraftingRecipe {
    companion object {
        class Builder {
            private val recipe = HiTechCraftingRecipe()

            fun add(item: Ingredient, count: Int): Builder {
                recipe.add(item, count)
                return this
            }

            fun cost(price: Int): Builder  {
                recipe.price(price)
                return this
            }

            fun result(newResult: ItemType): Builder {
                recipe.setResult(newResult)
                return this
            }

            fun result(newResult: ItemStack): Builder {
                recipe.setResult(newResult)
                return this
            }

            fun result(newResult: ItemStackSnapshot): Builder {
                recipe.setResult(newResult)
                return this
            }

            fun build(): HiTechCraftingRecipe {
                return  recipe
            }
        }

        fun builder() = Builder()
    }

    private val toMatch: HashMap<Ingredient, Int> = HashMap()
    private var cost: Int = 0
    private var result: ItemStackSnapshot = ItemStackSnapshot.NONE

    private fun add(item: Ingredient, count: Int) {
        toMatch[item] = toMatch[item]?: 0 + count
    }

    private fun price(price: Int) {
        cost = price
    }

    private fun setResult(newResult: ItemType) {
        setResult(ItemStack.of(newResult, 1))
    }

    private fun setResult(newResult: ItemStack) {
        setResult(newResult.createSnapshot())
    }

    private fun setResult(newResult: ItemStackSnapshot) {
        result = newResult
    }

    fun getCost(): Int {
        return cost
    }

    @Suppress("unused")
    fun previewRequirement(player: Player): List<ItemStack> {
        val list = ArrayList<ItemStack>()

        for (item in toMatch) {
            item.key.displayedItems()[0]?.createStack()
                ?.apply {
                    quantity = item.value

                    if (!hasEnoughIngredient(player, item.key, item.value)) {
                        val originalName= this[Keys.DISPLAY_NAME].orElse(null)?.toPlain()

                        if (originalName != null) {
                            offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, originalName))
                        } else {
                            offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, this.translation))
                        }
                    }
                }
                ?.let {
                    list += it
                }
        }

        return list
    }

    @Suppress("unused")
    fun hasEnoughDust(traveler: Traveler): Boolean {
        return traveler.starDust >= cost
    }

    private fun hasEnoughIngredient(player: Player, ingredient: Ingredient, count: Int): Boolean {
        val inv = player.inventory.query<MainPlayerInventory>(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory::class.java))

        var has = 0

        for (item in inv.slots<Slot>()) {
            item.peek().orElse(null)?.let {
                if (ingredient.test(it)) {
                    has += it.quantity
                }
            }
        }

        return has >= count
    }

    @Suppress("unused")
    fun hasEnoughIngredient(player: Player): Boolean {
        for (item in toMatch) {
            if (!hasEnoughIngredient(player, item.key, item.value)) {
                return false
            }
        }

        return true
    }

    private fun consume(player: Player, ingredient: Ingredient, count: Int) {
        val inv = player.inventory.query<MainPlayerInventory>(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory::class.java))

        var remain = count

        for (item in inv.slots<Slot>()) {
            item.peek().orElse(null)?.let {
                if (ingredient.test(it)) {
                    if (it.quantity >= remain) {
                        item.poll(remain)
                        remain = 0
                    } else {
                        remain -= it.quantity
                        item.poll()
                    }
                }
            }

            if (remain == 0) {
                break
            }
        }
    }

    @Suppress("unused")
    fun consume(player: Player, traveler: Traveler): Boolean {
        if (!hasEnoughDust(traveler) || !hasEnoughIngredient(player)) {
            return false
        }

        traveler.takeStarDust(cost)

        for (item in toMatch) {
            consume(player, item.key, item.value)
        }

        return true
    }

    @Suppress("unused")
    fun result(): ItemStack {
        return result.createStack()
    }
}
