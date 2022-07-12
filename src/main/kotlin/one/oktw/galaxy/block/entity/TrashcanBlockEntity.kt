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
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager

class TrashcanBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : Inventory, ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener {
    override fun clear() {}

    override fun size(): Int = 1

    override fun isEmpty(): Boolean = true

    override fun getStack(slot: Int): ItemStack = ItemStack.EMPTY

    override fun removeStack(slot: Int, amount: Int): ItemStack = ItemStack.EMPTY

    override fun removeStack(slot: Int): ItemStack = ItemStack.EMPTY

    override fun setStack(slot: Int, stack: ItemStack?) {}

    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return if (world!!.getBlockEntity(pos) !== this) {
            false
        } else player.squaredDistanceTo(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5) <= 64.0
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val gui = GUI.Builder(ScreenHandlerType.GENERIC_9X4)
            .setTitle(Text.of("Trashcan"))
            .apply {
                var i = 0
                val inv = FakeInventory(9 * 4)
                for (y in 0 until 4) for (x in 0 until 9) addSlot(x, y, Slot(inv, i++, 0, 0))
            }
            .build()

        GUISBackStackManager.openGUI(player as ServerPlayerEntity, gui)
        return ActionResult.SUCCESS
    }
}

class FakeInventory(invSize: Int) : Inventory {
    var inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(invSize, ItemStack.EMPTY)
    override fun clear() = inventory.clear()

    override fun size(): Int = inventory.size

    override fun isEmpty(): Boolean = true

    override fun getStack(slot: Int): ItemStack = inventory[slot]

    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(inventory, slot, amount)

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(inventory, slot)

    override fun setStack(slot: Int, stack: ItemStack?) {
        inventory[slot] = stack
    }

    override fun markDirty() {}

    override fun canPlayerUse(player: PlayerEntity?): Boolean = false
}
