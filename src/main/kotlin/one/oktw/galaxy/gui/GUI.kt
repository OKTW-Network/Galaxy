/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

package one.oktw.galaxy.gui

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.ScreenHandlerType.*
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.screen.slot.SlotActionType.*
import net.minecraft.text.Text
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.gui.utils.InventoryEditor
import one.oktw.galaxy.gui.utils.InventoryUtils
import one.oktw.galaxy.gui.utils.InventoryUtils.Companion.genericScreenHandlerType
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused", "MemberVisibilityCanBePrivate")
class GUI(private val type: ScreenHandlerType<out ScreenHandler>, private val title: Text) : NamedScreenHandlerFactory {
    private val inventory = when (type) {
        GENERIC_9X1 -> SimpleInventory(9)
        GENERIC_9X2 -> SimpleInventory(9 * 2)
        GENERIC_9X3 -> SimpleInventory(9 * 3)
        GENERIC_9X4 -> SimpleInventory(9 * 4)
        GENERIC_9X5 -> SimpleInventory(9 * 5)
        GENERIC_9X6 -> SimpleInventory(9 * 6)
        GENERIC_3X3 -> SimpleInventory(9)
        HOPPER -> SimpleInventory(5)
        else -> throw IllegalArgumentException("Unsupported container type: $type")
    }
    private val playerInventoryRange = inventory.size() until inventory.size() + 3 * 9
    private val playerHotBarRange = playerInventoryRange.last + 1..playerInventoryRange.last + 1 + 9
    private val bindings = ConcurrentHashMap<Int, GUIClickEvent.() -> Any>()
    private val rangeBindings = ConcurrentHashMap<Pair<IntRange, IntRange>, GUIClickEvent.() -> Any>()
    private val inventoryUtils = InventoryUtils(type)
    private var allowUseSlot = ConcurrentHashMap.newKeySet<Int>()

    override fun getDisplayName() = title

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        return when (type) {
            in genericScreenHandlerType -> GenericContainer(syncId, playerInventory)
            GENERIC_3X3 -> Generic3x3Container(syncId, playerInventory)
            HOPPER -> HopperContainer(syncId, playerInventory)
            else -> null
        }
    }

    fun getItem(x: Int, y: Int) = getItem(inventoryUtils.xyToIndex(x, y))

    fun getItem(index: Int): ItemStack {
        if (index !in 0 until inventory.size()) throw IndexOutOfBoundsException()

        return inventory.getStack(index)
    }

    fun setAllowUse(index: Int, canUse: Boolean) {
        if (index !in 0 until inventory.size()) throw IndexOutOfBoundsException("Allow use index out of inventory range")

        if (canUse) allowUseSlot.add(index) else allowUseSlot.remove(index)
    }

    fun setAllowUse(x: Int, y: Int, canUse: Boolean) {
        if (!checkRange(x, y)) throw IndexOutOfBoundsException("Allow use index out of inventory range")

        if (canUse) allowUseSlot.add(inventoryUtils.xyToIndex(x, y)) else allowUseSlot.remove(inventoryUtils.xyToIndex(x, y))
    }

    fun isAllowUse(index: Int) = allowUseSlot.contains(index)

    fun isAllowUse(x: Int, y: Int) = isAllowUse(inventoryUtils.xyToIndex(x, y))

    fun addBinding(index: Int, function: GUIClickEvent.() -> Any) {
        if (index !in 0 until inventory.size()) throw IndexOutOfBoundsException("Binding index out of inventory range")

        bindings[index] = function
    }

    fun addBinding(x: Int, y: Int, function: GUIClickEvent.() -> Any) {
        if (!checkRange(x, y)) throw IndexOutOfBoundsException("Binding index out of inventory range")

        bindings[x * y] = function
    }

    fun addBinding(xRange: IntRange, yRange: IntRange, function: GUIClickEvent.() -> Any) {
        if (!checkRange(xRange.first, yRange.first) || !checkRange(xRange.last, yRange.last)) {
            throw IndexOutOfBoundsException("Binding index out of inventory range")
        }

        rangeBindings[Pair(xRange, yRange)] = function
    }

    fun editInventory(block: InventoryEditor.() -> Unit) {
        val server = main?.server
        if (server != null && !server.isOnThread) {
            runBlocking(server.asCoroutineDispatcher()) {
                block.invoke(InventoryEditor(type, inventory))
                inventory.markDirty()
            }
        } else {
            block.invoke(InventoryEditor(type, inventory))
            inventory.markDirty()
        }
    }

    private fun checkRange(x: Int, y: Int) = inventoryUtils.xyToIndex(x, y) in 0 until inventory.size()

    // Vanilla container hack
    private inner class GenericContainer(syncId: Int, playerInventory: PlayerInventory) :
        net.minecraft.screen.GenericContainerScreenHandler(type, syncId, playerInventory, inventory, inventory.size() / 9) {
        override fun onSlotClick(slot: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack? {
            // Trigger binding TODO allow binding cancel player change
            if (slot in 0 until inventory.size()) {
                inventoryUtils.indexToXY(slot).let { (x, y) ->
                    bindings[slot]?.invoke(GUIClickEvent(x, y, action, inventory.getStack(slot)))
                    rangeBindings.filterKeys { (xRange, yRange) -> x in xRange && y in yRange }.values
                        .forEach { it.invoke(GUIClickEvent(x, y, action, inventory.getStack(slot))) }
                }
            }

            // Cancel player change inventory
            if (slot < inventory.size() && slot != -999 && slot !in allowUseSlot) {
                if (action == QUICK_CRAFT) endQuickCraft()
                return null
            }

            return when (action) {
                PICKUP, SWAP, CLONE, THROW, QUICK_CRAFT -> super.onSlotClick(slot, button, action, player)
                QUICK_MOVE -> {
                    if (slot in 0 until inventory.size() && slot !in allowUseSlot) return null

                    var itemStack = ItemStack.EMPTY
                    val inventorySlot = slots[slot]

                    if (inventorySlot != null && inventorySlot.hasStack()) {
                        val slotItemStack = inventorySlot.stack
                        itemStack = slotItemStack.copy()

                        // TODO move item to canUse slot
                        if (slot in playerInventoryRange) {
                            if (!insertItem(slotItemStack, playerHotBarRange.first, playerHotBarRange.last, false)) return null
                        } else if (slot in playerHotBarRange) {
                            if (!insertItem(slotItemStack, playerInventoryRange.first, playerInventoryRange.last, false)) return null
                        }

                        // clean up empty slot
                        if (slotItemStack.isEmpty) {
                            inventorySlot.stack = ItemStack.EMPTY
                        } else {
                            inventorySlot.markDirty()
                        }
                    }

                    return itemStack
                }
                PICKUP_ALL -> { // Rewrite PICKUP_ALL only take from allow use slot & player inventory.
                    if (slot < 0) return null

                    val cursorItemStack = player.inventory.cursorStack
                    val clickSlot = slots[slot]
                    if (!cursorItemStack.isEmpty && (!clickSlot.hasStack() || !clickSlot.canTakeItems(player))) {
                        var takeFullStack = false

                        loop@ while (!takeFullStack) { // First time only take not full stack items
                            takeFullStack = true
                            for (index in allowUseSlot + (inventory.size() until slots.size)) {
                                if (cursorItemStack.count >= cursorItemStack.maxCount) break@loop

                                val scanSlot = slots[index]
                                if (scanSlot.hasStack()
                                    && canInsertItemIntoSlot(scanSlot, cursorItemStack, true)
                                    && scanSlot.canTakeItems(player)
                                    && canInsertIntoSlot(cursorItemStack, scanSlot)
                                ) {
                                    val selectItemStack = scanSlot.stack
                                    if (takeFullStack || selectItemStack.count != selectItemStack.maxCount) {
                                        val takeCount = (cursorItemStack.maxCount - cursorItemStack.count).coerceAtMost(selectItemStack.count)
                                        val selectItemStack2 = scanSlot.takeStack(takeCount)
                                        cursorItemStack.increment(takeCount)
                                        if (selectItemStack2.isEmpty) {
                                            scanSlot.stack = ItemStack.EMPTY
                                        }

                                        scanSlot.onTakeItem(player, selectItemStack2)
                                    }
                                }
                            }
                        }
                    }

                    this.sendContentUpdates()

                    cursorItemStack
                }
            }
        }
    }

    private inner class Generic3x3Container(syncId: Int, playerInventory: PlayerInventory) :
        net.minecraft.screen.Generic3x3ContainerScreenHandler(syncId, playerInventory, inventory) {
        override fun onSlotClick(slot: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack? {
            // Trigger binding TODO allow binding cancel player change
            if (slot in 0 until inventory.size()) {
                inventoryUtils.indexToXY(slot).let { (x, y) ->
                    bindings[slot]?.invoke(GUIClickEvent(x, y, action, inventory.getStack(slot)))
                    rangeBindings.filterKeys { (xRange, yRange) -> x in xRange && y in yRange }.values
                        .forEach { it.invoke(GUIClickEvent(x, y, action, inventory.getStack(slot))) }
                }
            }

            // Cancel player change inventory
            if (slot < inventory.size() && slot != -999 && slot !in allowUseSlot) {
                if (action == QUICK_CRAFT) endQuickCraft()
                return null
            }

            return when (action) {
                PICKUP, SWAP, CLONE, THROW, QUICK_CRAFT -> super.onSlotClick(slot, button, action, player)
                QUICK_MOVE -> {
                    if (slot in 0 until inventory.size() && slot !in allowUseSlot) return null

                    var itemStack = ItemStack.EMPTY
                    val inventorySlot = slots[slot]

                    if (inventorySlot != null && inventorySlot.hasStack()) {
                        val slotItemStack = inventorySlot.stack
                        itemStack = slotItemStack.copy()

                        // TODO move item to canUse slot
                        if (slot in playerInventoryRange) {
                            if (!insertItem(slotItemStack, playerHotBarRange.first, playerHotBarRange.last, false)) return null
                        } else if (slot in playerHotBarRange) {
                            if (!insertItem(slotItemStack, playerInventoryRange.first, playerInventoryRange.last, false)) return null
                        }

                        // clean up empty slot
                        if (slotItemStack.isEmpty) {
                            inventorySlot.stack = ItemStack.EMPTY
                        } else {
                            inventorySlot.markDirty()
                        }
                    }

                    return itemStack
                }
                PICKUP_ALL -> { // Rewrite PICKUP_ALL only take from allow use slot & player inventory.
                    if (slot < 0) return null

                    val cursorItemStack = player.inventory.cursorStack
                    val clickSlot = slots[slot]
                    if (!cursorItemStack.isEmpty && (!clickSlot.hasStack() || !clickSlot.canTakeItems(player))) {
                        var takeFullStack = false

                        loop@ while (!takeFullStack) { // First time only take not full stack items
                            takeFullStack = true
                            for (index in allowUseSlot + (inventory.size() until slots.size)) {
                                if (cursorItemStack.count >= cursorItemStack.maxCount) break@loop

                                val scanSlot = slots[index]
                                if (scanSlot.hasStack()
                                    && canInsertItemIntoSlot(scanSlot, cursorItemStack, true)
                                    && scanSlot.canTakeItems(player)
                                    && canInsertIntoSlot(cursorItemStack, scanSlot)
                                ) {
                                    val selectItemStack = scanSlot.stack
                                    if (takeFullStack || selectItemStack.count != selectItemStack.maxCount) {
                                        val takeCount = (cursorItemStack.maxCount - cursorItemStack.count).coerceAtMost(selectItemStack.count)
                                        val selectItemStack2 = scanSlot.takeStack(takeCount)
                                        cursorItemStack.increment(takeCount)
                                        if (selectItemStack2.isEmpty) {
                                            scanSlot.stack = ItemStack.EMPTY
                                        }

                                        scanSlot.onTakeItem(player, selectItemStack2)
                                    }
                                }
                            }
                        }
                    }

                    this.sendContentUpdates()

                    cursorItemStack
                }
            }
        }
    }

    private inner class HopperContainer(syncId: Int, playerInventory: PlayerInventory) :
        net.minecraft.screen.HopperScreenHandler(syncId, playerInventory, inventory) {
        override fun onSlotClick(slot: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack? {
            // Trigger binding TODO allow binding cancel player change
            if (slot in 0 until inventory.size()) {
                inventoryUtils.indexToXY(slot).let { (x, y) ->
                    bindings[slot]?.invoke(GUIClickEvent(x, y, action, inventory.getStack(slot)))
                    rangeBindings.filterKeys { (xRange, yRange) -> x in xRange && y in yRange }.values
                        .forEach { it.invoke(GUIClickEvent(x, y, action, inventory.getStack(slot))) }
                }
            }

            // Cancel player change inventory
            if (slot < inventory.size() && slot != -999 && slot !in allowUseSlot) {
                if (action == QUICK_CRAFT) endQuickCraft()
                return null
            }

            return when (action) {
                PICKUP, SWAP, CLONE, THROW, QUICK_CRAFT -> super.onSlotClick(slot, button, action, player)
                QUICK_MOVE -> {
                    if (slot in 0 until inventory.size() && slot !in allowUseSlot) return null

                    var itemStack = ItemStack.EMPTY
                    val inventorySlot = slots[slot]

                    if (inventorySlot != null && inventorySlot.hasStack()) {
                        val slotItemStack = inventorySlot.stack
                        itemStack = slotItemStack.copy()

                        // TODO move item to canUse slot
                        if (slot in playerInventoryRange) {
                            if (!insertItem(slotItemStack, playerHotBarRange.first, playerHotBarRange.last, false)) return null
                        } else if (slot in playerHotBarRange) {
                            if (!insertItem(slotItemStack, playerInventoryRange.first, playerInventoryRange.last, false)) return null
                        }

                        // clean up empty slot
                        if (slotItemStack.isEmpty) {
                            inventorySlot.stack = ItemStack.EMPTY
                        } else {
                            inventorySlot.markDirty()
                        }
                    }

                    return itemStack
                }
                PICKUP_ALL -> { // Rewrite PICKUP_ALL only take from allow use slot & player inventory.
                    if (slot < 0) return null

                    val cursorItemStack = player.inventory.cursorStack
                    val clickSlot = slots[slot]
                    if (!cursorItemStack.isEmpty && (!clickSlot.hasStack() || !clickSlot.canTakeItems(player))) {
                        var takeFullStack = false

                        loop@ while (!takeFullStack) { // First time only take not full stack items
                            takeFullStack = true
                            for (index in allowUseSlot + (inventory.size() until slots.size)) {
                                if (cursorItemStack.count >= cursorItemStack.maxCount) break@loop

                                val scanSlot = slots[index]
                                if (scanSlot.hasStack()
                                    && canInsertItemIntoSlot(scanSlot, cursorItemStack, true)
                                    && scanSlot.canTakeItems(player)
                                    && canInsertIntoSlot(cursorItemStack, scanSlot)
                                ) {
                                    val selectItemStack = scanSlot.stack
                                    if (takeFullStack || selectItemStack.count != selectItemStack.maxCount) {
                                        val takeCount = (cursorItemStack.maxCount - cursorItemStack.count).coerceAtMost(selectItemStack.count)
                                        val selectItemStack2 = scanSlot.takeStack(takeCount)
                                        cursorItemStack.increment(takeCount)
                                        if (selectItemStack2.isEmpty) {
                                            scanSlot.stack = ItemStack.EMPTY
                                        }

                                        scanSlot.onTakeItem(player, selectItemStack2)
                                    }
                                }
                            }
                        }
                    }

                    this.sendContentUpdates()

                    cursorItemStack
                }
            }
        }
    }
}
