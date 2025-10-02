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

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
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

    abstract val ingredients: List<ItemStack>
    abstract val outputItem: CustomItem
    open val itemStack = outputItem.createItemStack()

    open fun isAffordable(player: PlayerEntity) = ingredients.all { ingredient ->
        var count = ingredient.count
        for (item in player.inventory.mainStacks) {
            if (ItemStack.areItemsAndComponentsEqual(item, ingredient)) count -= item.count
            if (count <= 0) break
        }
        count <= 0
    }

    open fun takeItem(player: PlayerEntity) {
        ingredients.forEach {
            var count = it.count
            while (count > 0) {
                val slot = player.inventory.getSlotWithStack(it)
                if (slot == -1) break // not found
                count -= player.inventory.removeStack(slot, count).count
            }
        }
    }

    open fun getOutputItem(player: PlayerEntity): ItemStack {
        // Show missing items
        val missing = ingredients.mapNotNull {
            var count = it.count
            for (item in player.inventory.mainStacks) {
                if (ItemStack.areItemsAndComponentsEqual(item, it)) count -= item.count
                if (count <= 0) break
            }
            if (count > 0) it.copyWithCount(count) else null
        }
        if (missing.isNotEmpty()) {
            val lore = listOf(
                // TODO Translate
                Text.literal("Missing:").styled { it.withColor(Formatting.RED).withBold(true).withItalic(false) },
                *missing.map {
                    it.itemName.copy().append(Text.literal("*")).append(Text.literal(it.count.toString()))
                        .styled { style -> style.withItalic(false).withColor(Formatting.WHITE) }
                }.toTypedArray()
            )
            val item = itemStack.copy()
            item.set(DataComponentTypes.LORE, LoreComponent(lore))
            return item
        } else {
            return itemStack.copy()
        }
    }
}
