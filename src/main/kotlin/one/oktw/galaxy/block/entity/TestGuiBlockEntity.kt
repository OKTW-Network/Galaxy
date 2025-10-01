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
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.item.Misc

class TestGuiBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener, Inventory {
    private val inventory = DefaultedList.ofSize(3 * 9, ItemStack.EMPTY)

    private val gui = GUI.Builder(ScreenHandlerType.GENERIC_9X6)
        .setTitle(Text.of("Test GUI"))
        .setBackground("A", Identifier.of("galaxy", "gui_font/container_layout/test_gui"))
        .blockEntity(this)
        .apply {
            var i = 0
            for (x in 0 until 9) addSlot(x, 0, Slot(this@TestGuiBlockEntity, i++, 0, 0))
            for (y in 4 until 6) for (x in 0 until 9) addSlot(x, y, Slot(this@TestGuiBlockEntity, i++, 0, 0))
        }.build().apply {
            editInventory {
                fill(0 until 9, 1 until 4, Misc.PLACEHOLDER.createItemStack())
                set(4, 2, Gui.CHECK_MARK.createItemStack())
                set(2, 2, Gui.PLUS.createItemStack())
            }
            addBinding(4, 2) {
                GUISBackStackManager.openGUI(player, gui2)
            }
            addBinding(2, 2) {
                GUISBackStackManager.openGUI(player, gui3)
            }
        }

    private val gui2 = GUI.Builder(ScreenHandlerType.GENERIC_9X4)
        .setTitle(Text.of("Test GUI2"))
        .setBackground("B", Identifier.of("galaxy", "gui_font/container_layout/test_gui"))
        .blockEntity(this)
        .apply {
            var i = 0
            for (y in 0 until 3) for (x in 0 until 9) addSlot(x, y, Slot(this@TestGuiBlockEntity, i++, 0, 0))
        }.build().apply {
            editInventory {
                fill(0 until 9, 3..3, Misc.PLACEHOLDER.createItemStack())
                set(4, 3, Gui.CROSS_MARK.createItemStack().apply { this.set(DataComponentTypes.ITEM_NAME, Text.of("CLOSE ALL")) })
            }
            addBinding(4, 3) {
                GUISBackStackManager.closeAll(player)
            }
        }
    private val gui3 = GUI.Builder(ScreenHandlerType.ANVIL)
        .setTitle(Text.literal("Test GUI3"))
        .setBackground("C", Identifier.of("galaxy", "gui_font/container_layout/test_gui"))
        .blockEntity(this).build()
        .apply {
            editInventory {
                set(0, Misc.PLACEHOLDER.createItemStack())
                set(1, Misc.PLACEHOLDER.createItemStack())
                set(2, Gui.CHECK_MARK.createItemStack())
            }
            addBinding(2) {
                player.sendMessage(Text.literal(inputText))
            }
        }

    override fun readCopyableData(view: ReadView) {
        Inventories.readData(view, inventory)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory)
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        GUISBackStackManager.openGUI(player as ServerPlayerEntity, gui)
        return ActionResult.SUCCESS_SERVER
    }

    override fun clear() {
        inventory.clear()
    }

    override fun size() = inventory.size

    override fun isEmpty() = inventory.isEmpty()

    override fun getStack(slot: Int) = inventory[slot]

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        val itemStack = Inventories.splitStack(inventory, slot, amount)
        if (!itemStack.isEmpty) {
            this.markDirty()
        }
        return itemStack
    }

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(inventory, slot)

    override fun setStack(slot: Int, stack: ItemStack) {
        inventory[slot] = stack
        if (stack.count > this.maxCountPerStack) {
            stack.count = this.maxCountPerStack
        }
        this.markDirty()
    }

    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return Inventory.canPlayerUse(this, player)
    }
}
