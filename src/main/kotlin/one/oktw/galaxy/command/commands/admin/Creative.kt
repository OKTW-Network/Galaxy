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
import net.minecraft.component.DataComponentTypes.ITEM_NAME
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.CustomItemBrowser
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.item.Misc

class Creative {
    private fun getListGui(): GUI {
        val itemBrowser = CustomItemBrowser(isCreative = true)
        val inventory = SimpleInventory(6 * 3)
        return GUI.Builder(ScreenHandlerType.GENERIC_9X3)
            .setTitle(Text.of("Galaxy"))
            .setBackground("A", Identifier.of("galaxy", "gui_font/container_layout/ht_crafting_table"))
            .apply {
                var i = 0
                for (y in 0..2) for (x in 2..7) {
                    addSlot(x, y, object : Slot(inventory, i++, 0, 0) {
                        val slotIndex = i - 1

                        override fun canInsert(stack: ItemStack) = false

                        override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
                            setStackNoCallbacks(itemBrowser.getCategoryItems()[slotIndex].createItemStack()) // refill
                            super.onTakeItem(player, stack)
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
                        inventory.clear()
                        for (i in 0..categoryItem.lastIndex) {
                            inventory.setStack(i, categoryItem[i].createItemStack())
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
                    if (action == SlotActionType.PICKUP) itemBrowser.previousCategory()
                }
                addBinding(0, 1) {
                    cancel = true // Cancel creative clone item
                }
                addBinding(0, 2) {
                    cancel = true
                    if (action == SlotActionType.PICKUP) itemBrowser.nextCategory()
                }

                // Handle Pages
                addBinding(8, 0) {
                    cancel = true
                    if (action == SlotActionType.PICKUP) itemBrowser.previousPage()
                }
                addBinding(8, 2) {
                    cancel = true
                    if (action == SlotActionType.PICKUP) itemBrowser.nextPage()
                }
            }
    }

    val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("creative")
        .executes {
            val player = it.source.playerOrThrow
            GUISBackStackManager.openGUI(player, getListGui())
            return@executes Command.SINGLE_SUCCESS
        }
}
