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

import net.minecraft.component.DataComponentTypes.LORE
import net.minecraft.component.type.LoreComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItem
import one.oktw.galaxy.item.Tool

abstract class CustomItemRecipe {
    companion object {
        val recipes = mapOf(
            // Block
            CustomBlockItem.HT_CRAFTING_TABLE to HTCraftingTable(),
            CustomBlockItem.ELEVATOR to Elevator(),
            CustomBlockItem.TRASHCAN to TrashCan(),
            CustomBlockItem.HARVEST to Harvest(),
            // Tool
            Tool.WRENCH to Wrench(),
            Tool.CROWBAR to Crowbar(),
        )
    }

    abstract val ingredients: List<CustomIngredient>
    abstract val outputItem: CustomItem

    open fun isAffordable(player: PlayerEntity) = ingredients.all {
        var count = it.count
        for (item in player.inventory.mainStacks) {
            if (it.test(item)) count -= item.count
            if (count <= 0) break
        }
        count <= 0
    }

    open fun takeItem(player: PlayerEntity) {
        ingredients.forEach {
            var count = it.count
            loop@ while (count > 0) {
                var found = false
                for (item in player.inventory.mainStacks) {
                    if (it.test(item)) {
                        count -= item.split(count).count
                        found = true
                    }
                    if (count <= 0) break@loop
                }
                if (!found) break@loop // not found
            }
        }
    }

    open fun getOutputItem(player: PlayerEntity): ItemStack {
        // Show missing items
        val missing = ingredients.mapNotNull {
            var count = it.count
            for (item in player.inventory.mainStacks) {
                if (it.test(item)) count -= item.count
                if (count <= 0) break
            }
            if (count > 0) it.getExample().first().copyWithCount(count) else null
        }
        if (missing.isNotEmpty()) {
            val lore = listOf(
                // TODO Translate
                Text.literal("Missing:").styled { it.withColor(Formatting.RED).withBold(true).withItalic(false) }, *missing.map { item ->
                    item.itemName.copy().styled { it.withColor(Formatting.WHITE).withItalic(false) }
                        .append(Text.literal(" * ").styled { it.withColor(Formatting.GRAY).withItalic(false) })
                        .append(Text.literal(item.count.toString()).styled { it.withColor(Formatting.GRAY).withItalic(false) })
                }.toTypedArray()
            )
            val item = outputItem.createItemStack()
            item.set(LORE, LoreComponent(lore))
            return item
        } else {
            return outputItem.createItemStack()
        }
    }

    open class CustomIngredient() {
        var stackMatch: ArrayList<ItemStack> = ArrayList()
        var itemMatch: ArrayList<Item> = ArrayList()
        var tagMatch: ArrayList<TagKey<Item>> = ArrayList()
        var count: Int = 1

        constructor(item: ItemStack, count: Int) : this() {
            this.addMatch(item).setCount(count)
        }

        constructor(item: ItemConvertible, count: Int) : this() {
            this.addMatch(item).setCount(count)
        }

        constructor(itemTags: TagKey<Item>, count: Int) : this() {
            this.addMatch(itemTags).setCount(count)
        }

        fun addMatch(itemStack: ItemStack): CustomIngredient {
            this.stackMatch += itemStack
            return this
        }

        fun addMatch(item: ItemConvertible): CustomIngredient {
            this.itemMatch += item.asItem()
            return this
        }

        fun addMatch(tag: TagKey<Item>): CustomIngredient {
            this.tagMatch += tag
            return this
        }

        fun setCount(count: Int): CustomIngredient {
            this.count = count
            return this
        }

        open fun getExample(): List<ItemStack> {
            if (stackMatch.isNotEmpty()) return stackMatch.map { it.copyWithCount(count) }
            if (itemMatch.isNotEmpty()) return itemMatch.map { it.defaultStack.copyWithCount(count) }
            if (tagMatch.isNotEmpty()) return tagMatch.flatMap { tag ->
                val items = Registries.ITEM.filter { it.registryEntry.isIn(tag) }
                // TODO Translate
                val lore = listOf(
                    Text.literal("Accept:").styled { it.withColor(Formatting.AQUA).withBold(true).withItalic(false) },
                    *items.map { item -> item.name.copy().styled { it.withColor(Formatting.WHITE).withItalic(false) } }.toTypedArray()
                )

                items.map { it.defaultStack.copyWithCount(count).apply { set(LORE, LoreComponent(lore)) } }
            }
            return emptyList()
        }

        open fun test(item: ItemStack): Boolean {
            return itemMatch.any { item.item == it } ||
                stackMatch.any { ItemStack.areItemsAndComponentsEqual(it, item) } ||
                tagMatch.any { item.isIn(it) }
        }
    }
}
