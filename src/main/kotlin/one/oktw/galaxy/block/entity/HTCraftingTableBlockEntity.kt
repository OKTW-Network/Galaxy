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

package one.oktw.galaxy.block.entity

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.CustomItemBrowser
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.item.Misc
import one.oktw.galaxy.item.recipe.CustomItemRecipe

class HTCraftingTableBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener {

    // TODO Recipe Manager
    private fun getListGui(): GUI {
        val itemBrowser = CustomItemBrowser()
        return GUI.Builder(ScreenHandlerType.GENERIC_9X3)
            .setTitle(Text.translatable("UI.Title.HiTechCraftingTableList"))
            .setBackground("A", Identifier.of("galaxy", "gui_font/container_layout/ht_crafting_table"))
            .blockEntity(this)
            .build()
            .apply {
                editInventory {
                    val updateView = {
                        fillAll(Misc.PLACEHOLDER.createItemStack())
                        // Category
                        val category = itemBrowser.getCategoryGui()
                        var i = 0
                        for (y in 0..2) {
                            val item = category.getOrNull(i++) ?: break
                            set(0, y, item.displayItem.createItemStack())
                        }

                        // Category Item
                        val categoryItem = itemBrowser.getCategoryItems()
                        i = 0
                        for (y in 0..2) for (x in 2..7) {
                            // TODO check recipe exist
                            val item = categoryItem.getOrNull(i++) ?: break
                            set(x, y, item.createItemStack())
                        }

                        // Category Paging
                        if (itemBrowser.isNextPageAvailable()) {
                            set(8, 0, Gui.ARROWHEAD_UP.createItemStack())
                        }
                        if (itemBrowser.isNextPageAvailable()) {
                            set(8, 2, Gui.ARROWHEAD_DOWN.createItemStack())
                        }
                    }
                    updateView()
                    itemBrowser.onPageUpdate { updateView() }
                }
                // Category Paging
                addBinding(0, 0) {
                    cancel = true
                    itemBrowser.previousCategory()
                }
                addBinding(0, 2) {
                    cancel = true
                    itemBrowser.nextCategory()
                }

                // Handle Items
                var i = 0
                for (y in 0..2) for (x in 2..7) {
                    addBinding(x, y) {
                        cancel = true
                        val item = itemBrowser.getItemByIndex(i++) ?: return@addBinding Unit
                        // TODO REPLACE with CustomItemRecipe function
                        val recipe = CustomItemRecipe.recipes[item] ?: return@addBinding Unit
                        GUISBackStackManager.openGUI(player, getRecipeGui(player, recipe.ingredients, recipe.outputItem.createItemStack()))
                    }
                }

                // Handle Pages
                addBinding(8, 0) {
                    cancel = true
                    itemBrowser.previousPage()
                }
                addBinding(8, 2) {
                    cancel = true
                    itemBrowser.nextPage()
                }
            }
    }

    private fun getRecipeGui(player: PlayerEntity, recipe: List<ItemStack>, itemStack: ItemStack): GUI {
        val output = SimpleInventory(itemStack.copy())
        return GUI.Builder(ScreenHandlerType.GENERIC_9X3)
            .setTitle(Text.translatable("UI.Title.HiTechCraftingTableRecipe", itemStack.itemName))
            .setBackground("B", Identifier.of("galaxy", "gui_font/container_layout/ht_crafting_table"))
            .blockEntity(this)
            .addSlot(7, 1, object : Slot(output, 0, 0, 0) {
                override fun canInsert(stack: ItemStack) = false

                override fun canTakeItems(player: PlayerEntity): Boolean {
                    // TODO REPLACE with CustomItemRecipe function
                    return recipe.all { recipe ->
                        var count = recipe.count
                        for (item in player.inventory.mainStacks) {
                            if (ItemStack.areItemsAndComponentsEqual(item, recipe)) count -= item.count
                            if (count <= 0) break
                        }
                        count <= 0
                    }
                }

                override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
                    // TODO REPLACE with CustomItemRecipe function
                    recipe.forEach {
                        var count = it.count
                        while (count > 0) {
                            val slot = player.inventory.getSlotWithStack(it)
                            if (slot == -1) break // not found
                            count -= player.inventory.removeStack(slot, count).count
                        }
                    }
                    setStackNoCallbacks(itemStack.copy()) // refill output
                    super.onTakeItem(player, stack)
                }
            })
            .setSkipPick(7, 1)
            .build().apply {
                editInventory {
                    fillAll(Misc.PLACEHOLDER.createItemStack())
                    var i = 0
                    for (y in 0..2) for (x in 1..4) {
                        val item = recipe.getOrNull(i++) ?: break
                        set(x, y, item)
                    }
                }
                onUpdate {
                    // Todo REPLACE with CustomItemRecipe function
                    // Show missing items
                    val missing = recipe.mapNotNull {
                        var count = it.count
                        for (item in player.inventory.mainStacks) {
                            if (ItemStack.areItemsAndComponentsEqual(item, it)) count -= item.count
                            if (count <= 0) break
                        }
                        if (count > 0) it.copyWithCount(count) else null
                    }
                    if (missing.isNotEmpty()) {
                        val lore = listOf(
                            Text.literal("Missing:").styled { it.withColor(Formatting.RED).withBold(true).withItalic(false) },
                            *missing.map {
                                it.itemName.copy().append(Text.literal("*")).append(Text.literal(it.count.toString()))
                                    .styled { style -> style.withItalic(false).withColor(Formatting.WHITE) }
                            }.toTypedArray()
                        )
                        val item = itemStack.copy()
                        item.set(DataComponentTypes.LORE, LoreComponent(lore))
                        output.setStack(0, item)
                    } else {
                        output.setStack(0, itemStack.copy())
                    }
                }
            }
    }


    override fun onClick(
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        GUISBackStackManager.openGUI(player as ServerPlayerEntity, getListGui())
        return ActionResult.SUCCESS_SERVER
    }
}
