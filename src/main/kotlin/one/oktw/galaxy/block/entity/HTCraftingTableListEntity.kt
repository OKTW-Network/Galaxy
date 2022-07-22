/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.*
import one.oktw.galaxy.recipe.HTCrafting.Recipes

class HTCraftingTableListEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem), CustomBlockClickListener, Inventory {
    // Rewrite
    private val inventory = DefaultedList.ofSize(3 * 9, ItemStack.EMPTY)
    private val gui = GUI.Builder(ScreenHandlerType.GENERIC_9X6).setTitle(Text.translatable("UI.Title.HiTechCraftingTableList")).build().apply {
        editInventory {
            // UI
            for (y in 2 until 5) set(8, y, Gui.EXTEND.createItemStack())
            for (x in 0 until 2) set(x, 0, Gui.EXTEND.createItemStack())
            set(2, 0, Button.ALL.createItemStack().setCustomName(Text.translatable("recipe.catalog.ALL").styled { it.withColor(Formatting.WHITE).withItalic(false) }))
            set(3, 0, CustomBlockItem.ELEVATOR.createItemStack().setCustomName(Text.translatable("recipe.catalog.MACHINE").styled { it.withColor(Formatting.WHITE).withItalic(false) }))
            set(4, 0, Tool.WRENCH.createItemStack().setCustomName(Text.translatable("recipe.catalog.TOOL").styled { it.withColor(Formatting.WHITE).withItalic(false) }))
            set(5, 0, Weapon.PISTOL_LASOR.createItemStack().setCustomName(Text.translatable("recipe.catalog.WEAPON").styled { it.withColor(Formatting.WHITE).withItalic(false) }))
            set(6, 0, Material.RAW_BASE_PLATE.createItemStack().setCustomName(Text.translatable("recipe.catalog.MATERIAL").styled { it.withColor(Formatting.WHITE).withItalic(false) }))
            set(7, 0, Gui.HTCT_TAB_1.createItemStack())
            set(8, 0, Gui.EXTEND.createItemStack())
            set(8, 1, Button.UNCLICKABLE_ARROW_UP.createItemStack().setCustomName(Text.translatable("UI.Button.PreviousPage").styled { it.withColor(Formatting.WHITE).withItalic(false) }))
            set(8, 5, Button.UNCLICKABLE_ARROW_DOWN.createItemStack().setCustomName(Text.translatable("UI.Button.NextPage").styled { it.withColor(Formatting.WHITE).withItalic(false) }))
        }
        // Category tab
        addBinding(2 until 7, 0..0) {
            val selected = this.x
            editInventory {
                set(7,
                    0,
                    when (selected) {
                        2 -> Gui.HTCT_TAB_1.createItemStack()
                        3 -> Gui.HTCT_TAB_2.createItemStack()
                        4 -> Gui.HTCT_TAB_3.createItemStack()
                        5 -> Gui.HTCT_TAB_4.createItemStack()
                        6 -> Gui.HTCT_TAB_5.createItemStack()
                        else -> Gui.EXTEND.createItemStack()
                    }
                )
            }
        }
        // Page up
        addBinding(8, 1) {
            // Todo
        }
        // Page down
        addBinding(8, 5) {
            // Todo
        }
    }

    override fun readCopyableData(nbt: NbtCompound) {
        Inventories.readNbt(nbt, inventory)
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        GUISBackStackManager.openGUI(player as ServerPlayerEntity, gui)
        return ActionResult.SUCCESS
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
        return if (world!!.getBlockEntity(pos) !== this) {
            false
        } else player.squaredDistanceTo(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5) <= 64.0
    }
}
