/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.block.pipe

import net.minecraft.block.ChestBlock
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.entity.PipeBlockEntity
import one.oktw.galaxy.block.pipe.PipeUtil.canMergeWith
import one.oktw.galaxy.block.pipe.PipeUtil.getAvailableSlots
import java.lang.Integer.min
import java.util.*

open class PipeSideExport(pipe: PipeBlockEntity, side: Direction, id: UUID = UUID.randomUUID()) : PipeSide(pipe, side, id, PipeSideMode.EXPORT) {
    override fun output(item: ItemStack): ItemStack {
        val world = pipe.world as ServerWorld
        val targetPos = pipe.pos.offset(side)

        val inventory = when (val blockEntity = world.getBlockEntity(targetPos)) {
            is InventoryProvider -> blockEntity.getInventory(world.getBlockState(targetPos), world, targetPos)
            is ChestBlockEntity -> {
                val blockState = world.getBlockState(targetPos)
                ChestBlock.getInventory(blockState.block as ChestBlock, blockState, world, targetPos, true)
            }
            is Inventory -> blockEntity
            else -> null
        } ?: return item

        inventory.getAvailableSlots(side.opposite).forEach {
            if (item.isEmpty) return ItemStack.EMPTY
            if (!inventory.isValid(it, item) || (inventory as? SidedInventory)?.canInsert(it, item, side.opposite) == false) return@forEach

            val stack = inventory.getStack(it)
            if (stack.isEmpty) {
                inventory.setStack(it, item)
                return ItemStack.EMPTY
            } else if (item.canMergeWith(stack)) {
                val i = min(item.count, stack.maxCount - stack.count)
                stack.increment(i)
                item.decrement(i)
            }
        }

        return item
    }
}
