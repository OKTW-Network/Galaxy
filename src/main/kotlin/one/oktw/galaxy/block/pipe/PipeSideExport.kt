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

import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.entity.PipeBlockEntity
import one.oktw.galaxy.block.pipe.PipeUtil.canMergeWith
import one.oktw.galaxy.block.pipe.PipeUtil.getAvailableSlots
import java.lang.Integer.min
import java.lang.ref.WeakReference
import java.util.*

open class PipeSideExport(pipe: PipeBlockEntity, side: Direction, id: UUID = UUID.randomUUID()) : PipeSide(pipe, side, id, PipeSideMode.EXPORT) {
    private var inventoryCache = WeakReference<Inventory>(null)
    private var fullCache: Boolean? = false

    override fun tick() {
        // Null cache
        inventoryCache = WeakReference(null)
        fullCache = null
    }

    fun canExport(item: ItemStack): Boolean {
        val inventory = inventoryCache.get() ?: getInventory()?.also { inventoryCache = WeakReference(it) } ?: return false

        inventory.getAvailableSlots(side.opposite).any { slot ->
            inventory.isValid(slot, item) &&
                (inventory as? SidedInventory)?.canInsert(slot, item, side.opposite) != false &&
                inventory.getStack(slot).let { it.isEmpty || it.count < it.maxCount && it.canMergeWith(item) }
        }

        return false
    }

    fun isFull(): Boolean {
        return fullCache ?: (inventoryCache.get() ?: getInventory())?.also { inventoryCache = WeakReference(it) }?.let { inventory ->
            inventory.getAvailableSlots(side.opposite).all { slot -> inventory.getStack(slot).let { !it.isStackable || it.count >= it.maxCount } }
        }?.also { fullCache = it } ?: true
    }

    override fun output(item: ItemStack): ItemStack {
        val inventory = getInventory() ?: return item

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

        return item.also { if (!it.isEmpty) fullCache = true }
    }

    private fun getInventory() = PipeUtil.getInventory(pipe.world as ServerWorld, pipe.pos.offset(side))
}
