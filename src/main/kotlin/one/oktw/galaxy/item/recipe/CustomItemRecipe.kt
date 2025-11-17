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

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents.LORE
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.level.ItemLike
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

    open fun isAffordable(player: Player) = ingredients.all {
        var count = it.count
        for (item in player.inventory.nonEquipmentItems) {
            if (it.test(item)) count -= item.count
            if (count <= 0) break
        }
        count <= 0
    }

    open fun takeItem(player: Player) {
        ingredients.forEach {
            var count = it.count
            loop@ while (count > 0) {
                var found = false
                for (item in player.inventory.nonEquipmentItems) {
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

    open fun getOutputItem(player: Player): ItemStack {
        // Show missing items
        val missing = ingredients.mapNotNull {
            var count = it.count
            for (item in player.inventory.nonEquipmentItems) {
                if (it.test(item)) count -= item.count
                if (count <= 0) break
            }
            if (count > 0) it.getExample().first().copyWithCount(count) else null
        }
        if (missing.isNotEmpty()) {
            val lore = listOf(
                // TODO Translate
                Component.literal("Missing:").withStyle { it.withColor(ChatFormatting.RED).withBold(true).withItalic(false) }, *missing.map { item ->
                    item.itemName.copy().withStyle { it.withColor(ChatFormatting.WHITE).withItalic(false) }
                        .append(Component.literal(" * ").withStyle { it.withColor(ChatFormatting.GRAY).withItalic(false) })
                        .append(Component.literal(item.count.toString()).withStyle { it.withColor(ChatFormatting.GRAY).withItalic(false) })
                }.toTypedArray()
            )
            val item = outputItem.createItemStack()
            item.set(LORE, ItemLore(lore))
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

        constructor(item: ItemLike, count: Int) : this() {
            this.addMatch(item).setCount(count)
        }

        constructor(itemTags: TagKey<Item>, count: Int) : this() {
            this.addMatch(itemTags).setCount(count)
        }

        fun addMatch(itemStack: ItemStack): CustomIngredient {
            this.stackMatch += itemStack
            return this
        }

        fun addMatch(item: ItemLike): CustomIngredient {
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
            if (itemMatch.isNotEmpty()) return itemMatch.map { it.defaultInstance.copyWithCount(count) }
            if (tagMatch.isNotEmpty()) return tagMatch.flatMap { tag ->
                val items = BuiltInRegistries.ITEM.filter { it.builtInRegistryHolder().`is`(tag) }
                // TODO Translate
                val lore = listOf(
                    Component.literal("Accept:").withStyle { it.withColor(ChatFormatting.AQUA).withBold(true).withItalic(false) },
                    *items.map { item -> item.name.copy().withStyle { it.withColor(ChatFormatting.WHITE).withItalic(false) } }.toTypedArray()
                )

                items.map { it.defaultInstance.copyWithCount(count).apply { set(LORE, ItemLore(lore)) } }
            }
            return emptyList()
        }

        open fun test(item: ItemStack): Boolean {
            return itemMatch.any { item.item == it } ||
                stackMatch.any { ItemStack.isSameItemSameComponents(it, item) } ||
                tagMatch.any { item.`is`(it) }
        }
    }
}
