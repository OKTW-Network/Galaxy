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

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.phys.BlockHitResult
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Misc

class TrashcanBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : Container, ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener {
    override fun clearContent() {}

    override fun getContainerSize(): Int = 1

    override fun isEmpty(): Boolean = true

    override fun getItem(slot: Int): ItemStack = ItemStack.EMPTY

    override fun removeItem(slot: Int, amount: Int): ItemStack = ItemStack.EMPTY

    override fun removeItemNoUpdate(slot: Int): ItemStack = ItemStack.EMPTY

    override fun setItem(slot: Int, stack: ItemStack) {}

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun onClick(player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        if (player.isSpectator) {
            return InteractionResult.CONSUME
        }

        val gui = GUI
            .Builder(MenuType.GENERIC_9x6)
            .setTitle(Component.translatable("block.TRASHCAN"))
            .setBackground("A", ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/container_layout/trashcan"))
            .blockEntity(this)
            .apply {
                var i = 0
                val inv = SimpleContainer(9 * 6)

                for (y in 0 until 6) for (x in 0 until 6) addSlot(x, y, Slot(inv, i++, 0, 0))
            }
            .build()
            .apply { editInventory { fillAll(Misc.PLACEHOLDER.createItemStack()) } }

        GUISBackStackManager.openGUI(player as ServerPlayer, gui)

        return InteractionResult.SUCCESS_SERVER
    }
}
