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
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
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
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.player.PlayerHelper

class HTCraftingTableBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener {

    // TODO Recipe Manager
    private val listGui = GUI.Builder(ScreenHandlerType.GENERIC_9X3)
        .setTitle(Text.translatable("UI.Title.HiTechCraftingTableList"))
        .setBackground("A", Identifier.of("galaxy", "gui_font/container_layout/ht_crafting_table"))
        .blockEntity(this)
        .build()
        .apply {
            editInventory {
                set(0, 1, CustomBlockItem.HT_CRAFTING_TABLE.createItemStack())
                set(2, 0, CustomBlockItem.HT_CRAFTING_TABLE.createItemStack())
                set(3, 0, CustomBlockItem.ELEVATOR.createItemStack())
                set(4, 0, CustomBlockItem.ANGEL_BLOCK.createItemStack())
                set(5, 0, CustomBlockItem.HARVEST.createItemStack())
                set(6, 0, CustomBlockItem.TRASHCAN.createItemStack())
            }
            addBinding(2, 0) {
                GUISBackStackManager.openGUI(player, getRecipeGui(CustomBlockItem.HT_CRAFTING_TABLE.createItemStack()))
            }
            addBinding(3, 0) {
                GUISBackStackManager.openGUI(player, getRecipeGui(CustomBlockItem.ELEVATOR.createItemStack()))
            }
            addBinding(4, 0) {
                GUISBackStackManager.openGUI(player, getRecipeGui(CustomBlockItem.ANGEL_BLOCK.createItemStack()))
            }
            addBinding(5, 0) {
                GUISBackStackManager.openGUI(player, getRecipeGui(CustomBlockItem.HARVEST.createItemStack()))
            }
            addBinding(6, 0) {
                GUISBackStackManager.openGUI(player, getRecipeGui(CustomBlockItem.TRASHCAN.createItemStack()))
            }
        }

    private fun getRecipeGui(itemStack: ItemStack): GUI {
        return GUI.Builder(ScreenHandlerType.GENERIC_9X3)
            .setTitle(Text.translatable("UI.Title.HiTechCraftingTableRecipe", itemStack.itemName))
            .setBackground("B", Identifier.of("galaxy", "gui_font/container_layout/ht_crafting_table"))
            .blockEntity(this)
            .build()
            .apply {
                editInventory {
                    set(7, 1, itemStack)
                }
                addBinding(7, 1) {
                    PlayerHelper.giveItemToPlayer(player, itemStack)
                    player.closeHandledScreen()
                }
            }
    }


    override fun onClick(
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        GUISBackStackManager.openGUI(player as ServerPlayerEntity, listGui)
        return ActionResult.SUCCESS_SERVER
    }
}
