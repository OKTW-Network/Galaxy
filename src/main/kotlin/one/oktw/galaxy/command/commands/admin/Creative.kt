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

package one.oktw.galaxy.command.commands.admin

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.component.DataComponents.ITEM_NAME
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.CustomItemBrowser
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.item.Misc
import one.oktw.galaxy.item.gui.GuiButton
import one.oktw.galaxy.item.gui.GuiIcon
import one.oktw.galaxy.item.gui.GuiModelBuilder

class Creative {
    private val previousPageButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.ARROWHEAD_UP).build(),
        Component.translatable("UI.Button.PreviousPage")
    ).createItemStack()
    private val nextPageButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.ARROWHEAD_DOWN).build(),
        Component.translatable("UI.Button.NextPage")
    ).createItemStack()

    private fun getListGui(): GUI {
        val itemBrowser = CustomItemBrowser()
        val inventory = SimpleContainer(6 * 3)
        return GUI.Builder(MenuType.GENERIC_9x3)
            .setTitle(Component.literal("Galaxy"))
            .setBackground("A", Identifier.fromNamespaceAndPath("galaxy", "gui_font/container_layout/ht_crafting_table"))
            .apply {
                var i = 0
                for (y in 0..2) for (x in 2..7) {
                    addSlot(x, y, object : Slot(inventory, i++, 0, 0) {
                        val slotIndex = i - 1

                        override fun mayPlace(stack: ItemStack) = false

                        override fun onTake(player: Player, stack: ItemStack) {
                            set(itemBrowser.getCategoryItems()[slotIndex].createItemStack()) // refill
                            super.onTake(player, stack)
                        }
                    })
                    setSkipPick(x, y)
                }
            }
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
                        inventory.clearContent()
                        for (i in 0..categoryItem.lastIndex) {
                            inventory.setItem(i, categoryItem[i].createItemStack())
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

    val command: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("creative")
        .executes {
            val player = it.source.playerOrException
            GUISBackStackManager.openGUI(player, getListGui())
            return@executes Command.SINGLE_SUCCESS
        }
}
