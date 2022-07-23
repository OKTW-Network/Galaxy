/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

package one.oktw.galaxy.recipe.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import one.oktw.galaxy.item.CustomItem
import one.oktw.galaxy.util.ItemLoreBuilder

class HTCraftingRecipe {
    private val ingredientList: ArrayList<Ingredient> = ArrayList()
    private val toMatch: HashMap<Ingredient, Int> = HashMap()
    private var cost: Int = 0
    private var result: ItemStack = ItemStack.EMPTY
    private var customItemResult: CustomItem? = null

    fun add(item: Ingredient, count: Int) {
        if (!ingredientList.contains(item)) {
            ingredientList.add(item)
        }

        toMatch[item] = toMatch[item] ?: (0 + count)
    }

    fun price(price: Int) {
        cost = price
    }

    fun setResult(newResult: Item) {
        setResult(newResult.defaultStack)
    }

    fun setResult(newResult: ItemStack) {
        result = newResult
    }

    fun setCustomResult(newResult: CustomItem) {
        customItemResult = newResult
    }

    fun getCost(): Int {
        return cost
    }

    fun previewRequirement(player: ServerPlayerEntity): List<ItemStack> {
        val list = ArrayList<ItemStack>()

        for (item in ingredientList) {
            val quantity = toMatch[item] ?: continue
            item.genPreviewItem()
                .apply {
                    if (!haveEnoughIngredient(player, item, quantity)) {
                        this.setCustomName(this.name.copy().styled { it.withColor(Formatting.RED) })
                    }
                }
                .let {
                    list += ItemLoreBuilder(it)
                        .addText(Text.translatable("UI.Tip.needItemCount", quantity))
                        .addText(Text.translatable("UI.Tip.haveItemCount", haveIngredient(player, item)))
                        .apply()
                }
        }

        return list
    }

    fun haveEnoughDust(): Boolean {
        // TODO "Waiting for Stardust System"
        return true
    }

    private fun haveIngredient(player: ServerPlayerEntity, ingredient: Ingredient): Int {
        val inv = player.inventory
        var has = 0

        for (index in 0 until inv.main.size) {
            val item = inv.getStack(index)
            if (ingredient.matches(item)) {
                has += item.count
            }
        }

        return has
    }

    private fun haveEnoughIngredient(player: ServerPlayerEntity, ingredient: Ingredient, count: Int): Boolean {
        val has = haveIngredient(player, ingredient)

        return has >= count
    }

    fun haveEnoughIngredient(player: ServerPlayerEntity): Boolean {
        for (item in toMatch) {
            if (!haveEnoughIngredient(player, item.key, item.value)) {
                return false
            }
        }

        return true
    }

    private fun consume(player: ServerPlayerEntity, ingredient: Ingredient, count: Int) {
        val inv = player.inventory
        var remain = count

        for (index in 0 until inv.main.size) {
            val item = inv.getStack(index)
            if (ingredient.matches(item)) {
                if (item.count >= remain) {
                    item.decrement(remain)
                    remain = 0
                } else {
                    remain -= item.count
                    item.decrement(item.count)
                }
            }
            if (remain == 0) break
        }
    }

    fun consume(player: ServerPlayerEntity): Boolean {
        // Todo Waiting for Stardust System
        return true
    }

    fun result(): ItemStack {
        return customItemResult.let {
            it?.createItemStack() ?: result
        }
    }

    // Todo add Stardust
    fun previewResult(player: ServerPlayerEntity): ItemStack {
        val list = ArrayList<Text>()
        var enough = true
        val newResult = result.copy()

        ingredientList.forEach { ingredient ->
            val count = toMatch[ingredient] ?: return@forEach
            val has = haveIngredient(player, ingredient)
            val item = ingredient.genPreviewItem()

            if (has < count) enough = false

            list += Text.of("$has / $count").copy().styled { it.withColor(if (has < count) Formatting.RED else Formatting.GREEN) }
            list += item.name.copy().styled { it.withColor(if (has < count) Formatting.RED else Formatting.GREEN) }
        }

        if (!enough) {
            newResult.setCustomName(newResult.name.copy().styled { it.withColor(Formatting.RED) })
        }

        return ItemLoreBuilder(newResult).addText(list).apply()
    }
}
