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
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
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
        return Inventory.canPlayerUse(this, player)
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (player.isSpectator) {
            return ActionResult.CONSUME
        }

        val gui = GUI
            .Builder(ScreenHandlerType.GENERIC_9X4)
            .setTitle(Text.of("Trashcan"))
            .blockEntity(this)
            .apply {
                var i = 0
                val inv = SimpleInventory(9 * 4)

                for (y in 0 until 4) for (x in 0 until 9) addSlot(x, y, Slot(inv, i++, 0, 0))
            }
            .build()

        GUISBackStackManager.openGUI(player as ServerPlayerEntity, gui)

        return ActionResult.SUCCESS_SERVER
    }
}
