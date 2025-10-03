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
import net.minecraft.component.DataComponentTypes.ITEM_NAME
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
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

    private fun getListGui(): GUI {
        val itemBrowser = CustomItemBrowser(filterRecipe = true)
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
                            set(0, y, item.displayItem.createItemStack().apply { set(ITEM_NAME, item.displayName) })
                        }

                        // Category Item
                        val categoryItem = itemBrowser.getCategoryItems()
                        i = 0
                        for (y in 0..2) for (x in 2..7) {
                            val item = categoryItem.getOrNull(i++) ?: break
                            set(x, y, item.createItemStack())
                        }

                        // Category Paging
                        if (itemBrowser.isPreviousPageAvailable()) {
                            set(8, 0, Gui.ARROWHEAD_UP.createItemStack().apply { set(ITEM_NAME, Text.translatable("UI.Button.PreviousPage")) })
                        }
                        if (itemBrowser.isNextPageAvailable()) {
                            set(8, 2, Gui.ARROWHEAD_DOWN.createItemStack().apply { set(ITEM_NAME, Text.translatable("UI.Button.NextPage")) })
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
                addBinding(2..7, 0..2) {
                    cancel = true
                    // Slot is 6 x 3
                    val index = this.y * 6 + (this.x - 2)
                    val item = itemBrowser.getItemByIndex(index) ?: return@addBinding Unit
                    val recipe = CustomItemRecipe.recipes[item] ?: return@addBinding Unit
                    GUISBackStackManager.openGUI(player, getRecipeGui(player, recipe))
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

    private fun getRecipeGui(player: PlayerEntity, recipe: CustomItemRecipe): GUI {
        val output = SimpleInventory(recipe.outputItem.createItemStack())
        return GUI.Builder(ScreenHandlerType.GENERIC_9X3)
            .setTitle(Text.translatable("UI.Title.HiTechCraftingTableRecipe", recipe.outputItem.getName()))
            .setBackground("B", Identifier.of("galaxy", "gui_font/container_layout/ht_crafting_table"))
            .blockEntity(this)
            .addSlot(7, 1, object : Slot(output, 0, 0, 0) {
                override fun canInsert(stack: ItemStack) = false

                override fun canTakeItems(player: PlayerEntity): Boolean = recipe.isAffordable(player)

                override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
                    recipe.takeItem(player)
                    setStackNoCallbacks(recipe.outputItem.createItemStack()) // refill output
                    super.onTakeItem(player, stack)
                }
            })
            .setSkipPick(7, 1)
            .build().apply {
                editInventory {
                    fillAll(Misc.PLACEHOLDER.createItemStack())
                    var i = 0
                    for (y in 0..2) for (x in 1..4) {
                        val item = recipe.ingredients.getOrNull(i++) ?: break
                        set(x, y, item)
                    }
                }
                onUpdate {
                    output.setStack(0, recipe.getOutputItem(player))
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
