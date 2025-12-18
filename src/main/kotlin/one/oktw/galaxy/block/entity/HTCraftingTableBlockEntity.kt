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

import kotlinx.coroutines.*
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents.ITEM_NAME
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.phys.BlockHitResult
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.CustomItemBrowser
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.item.Misc
import one.oktw.galaxy.item.gui.GuiButton
import one.oktw.galaxy.item.gui.GuiIcon
import one.oktw.galaxy.item.gui.GuiModelBuilder
import one.oktw.galaxy.item.recipe.CustomItemRecipe

class HTCraftingTableBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    private val previousPageButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.ARROWHEAD_UP).build(),
        Component.translatable("UI.Button.PreviousPage")
    ).createItemStack()
    private val nextPageButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.ARROWHEAD_DOWN).build(),
        Component.translatable("UI.Button.NextPage")
    ).createItemStack()

    private fun getListGui(): GUI {
        val itemBrowser = CustomItemBrowser(filterRecipe = true)
        return GUI.Builder(MenuType.GENERIC_9x3)
            .setTitle(Component.translatable("UI.Title.HiTechCraftingTableList"))
            .setBackground("A", Identifier.fromNamespaceAndPath("galaxy", "gui_font/container_layout/ht_crafting_table"))
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
                            set(8, 0, previousPageButton)
                        }
                        if (itemBrowser.isNextPageAvailable()) {
                            set(8, 2, nextPageButton)
                        }
                    }
                    updateView()
                    itemBrowser.onPageUpdate { updateView() }
                }
                // Category Paging
                addBinding(0, 0) {
                    cancel = true
                    if (action == ClickType.PICKUP) itemBrowser.previousCategory()
                }
                addBinding(0, 1) {
                    cancel = true // Cancel creative clone item
                }
                addBinding(0, 2) {
                    cancel = true
                    if (action == ClickType.PICKUP) itemBrowser.nextCategory()
                }

                // Handle Items
                addBinding(2..7, 0..2) {
                    cancel = true
                    if (action != ClickType.PICKUP) return@addBinding Unit
                    // Slot is 6 x 3
                    val index = this.y * 6 + (this.x - 2)
                    val item = itemBrowser.getItemByIndex(index) ?: return@addBinding Unit
                    val recipe = CustomItemRecipe.recipes[item] ?: return@addBinding Unit
                    GUISBackStackManager.openGUI(player, getRecipeGui(player, recipe))
                }

                // Handle Pages
                addBinding(8, 0) {
                    cancel = true
                    if (action == ClickType.PICKUP) itemBrowser.previousPage()
                }
                addBinding(8, 2) {
                    cancel = true
                    if (action == ClickType.PICKUP) itemBrowser.nextPage()
                }
            }
    }

    private fun getRecipeGui(player: Player, recipe: CustomItemRecipe): GUI {
        val output = SimpleContainer(recipe.outputItem.createItemStack())
        val gui =
            GUI.Builder(MenuType.GENERIC_9x3)
                .setTitle(Component.translatable("UI.Title.HiTechCraftingTableRecipe", recipe.outputItem.getName()))
                .setBackground("B", Identifier.fromNamespaceAndPath("galaxy", "gui_font/container_layout/ht_crafting_table"))
                .blockEntity(this)
                .addSlot(7, 1, object : Slot(output, 0, 0, 0) {
                    override fun mayPlace(stack: ItemStack) = false

                    override fun mayPickup(player: Player): Boolean = recipe.isAffordable(player)

                    override fun onTake(player: Player, stack: ItemStack) {
                        recipe.takeItem(player)
                        set(recipe.outputItem.createItemStack()) // refill output
                        super.onTake(player, stack)
                    }
                })
                .setSkipPick(7, 1)
                .build().apply {
                    editInventory {
                        fillAll(Misc.PLACEHOLDER.createItemStack())
                        var i = 0
                        loop@ for (y in 0..2) for (x in 1..4) {
                            val item = recipe.ingredients.getOrNull(i++)?.getExample()?.first() ?: break@loop
                            set(x, y, item)
                        }
                    }
                    addBinding(7, 1) {
                        // Allow creative clone true item
                        if (action == ClickType.CLONE && player.isCreative) {
                            cancel = true
                            val screen = player.containerMenu
                            if (screen.carried.isEmpty) screen.carried = recipe.outputItem.createItemStack().apply { count = maxStackSize }
                        }
                    }
                    onUpdate {
                        output.setItem(0, recipe.getOutputItem(player))
                    }
                }

        // Multi item display animation
        val job = launch {
            var loop = 0
            while (!(player as ServerPlayer).hasDisconnected()) {
                gui.editInventory {
                    var i = 0
                    loop@ for (y in 0..2) for (x in 1..4) {
                        val item = recipe.ingredients.getOrNull(i++)?.getExample() ?: break@loop
                        if (item.count() > 1) {
                            set(x, y, item[loop % item.count()])
                        }
                    }
                    loop++
                }
                delay(1000)
            }
        }
        gui.onClose { job.cancel() }

        return gui
    }

    override fun onClick(
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        GUISBackStackManager.openGUI(player as ServerPlayer, getListGui())
        return InteractionResult.SUCCESS_SERVER
    }
}
