/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.ScreenHandlerType.*
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.screen.slot.SlotActionType.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.gui.utils.InventoryEditor
import one.oktw.galaxy.gui.utils.InventoryUtils
import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused", "MemberVisibilityCanBePrivate")
class GUI private constructor(private val type: ScreenHandlerType<out ScreenHandler>, private val title: Text, private val slotBindings: HashMap<Int, Slot>) :
    NamedScreenHandlerFactory {
    private val inventory = when (type) {
        GENERIC_9X1, GENERIC_3X3 -> SimpleInventory(9)
        GENERIC_9X2 -> SimpleInventory(9 * 2)
        GENERIC_9X3 -> SimpleInventory(9 * 3)
        GENERIC_9X4 -> SimpleInventory(9 * 4)
        GENERIC_9X5 -> SimpleInventory(9 * 5)
        GENERIC_9X6 -> SimpleInventory(9 * 6)
        HOPPER -> SimpleInventory(5)
        else -> throw IllegalArgumentException("Unsupported container type: $type")
    }
    private val playerInventoryRange = inventory.size() until inventory.size() + 3 * 9
    private val playerHotBarRange = playerInventoryRange.last + 1..playerInventoryRange.last + 1 + 9
    private val bindings = ConcurrentHashMap<Int, GUIClickEvent.() -> Any>()
    private val rangeBindings = ConcurrentHashMap<Pair<IntRange, IntRange>, GUIClickEvent.() -> Any>()
    private val inventoryUtils = InventoryUtils(type)
    private val openListener = ConcurrentHashMap.newKeySet<(PlayerEntity) -> Any>()
    private val closeListener = ConcurrentHashMap.newKeySet<(PlayerEntity) -> Any>()

    override fun getDisplayName() = title

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return GuiContainer(syncId, playerInventory)
    }

    fun getItem(x: Int, y: Int) = getItem(inventoryUtils.xyToIndex(x, y))

    fun getItem(index: Int): ItemStack {
        if (index !in 0 until inventory.size()) throw IndexOutOfBoundsException()

        return inventory.getStack(index)
    }

    fun addBinding(index: Int, function: GUIClickEvent.() -> Any) {
        if (index !in 0 until inventory.size()) throw IndexOutOfBoundsException("Binding index out of inventory range")

        bindings[index] = function
    }

    fun addBinding(x: Int, y: Int, function: GUIClickEvent.() -> Any) {
        if (!checkRange(x, y)) throw IndexOutOfBoundsException("Binding index out of inventory range")

        bindings[inventoryUtils.xyToIndex(x, y)] = function
    }

    fun addBinding(xRange: IntRange, yRange: IntRange, function: GUIClickEvent.() -> Any) {
        if (!checkRange(xRange.first, yRange.first) || !checkRange(xRange.last, yRange.last)) {
            throw IndexOutOfBoundsException("Binding index out of inventory range")
        }

        rangeBindings[Pair(xRange, yRange)] = function
    }

    fun editInventory(block: suspend InventoryEditor.() -> Unit) {
        val server = main!!.server
        main!!.launch(server.asCoroutineDispatcher(), if (server.isOnThread) CoroutineStart.UNDISPATCHED else CoroutineStart.DEFAULT) {
            block.invoke(InventoryEditor(type, inventory))
            inventory.markDirty()
        }
    }

    fun onOpen(block: (PlayerEntity) -> Any) {
        openListener += block
    }

    fun onClose(block: (PlayerEntity) -> Any) {
        closeListener += block
    }

    private fun checkRange(x: Int, y: Int) = inventoryUtils.xyToIndex(x, y) in 0 until inventory.size()

    class Builder(private val type: ScreenHandlerType<out ScreenHandler>) {
        private val logger = LogManager.getLogger()
        private val inventoryUtils = InventoryUtils(type)
        private var title: Text = Text.empty()
        private val slotBindings = HashMap<Int, Slot>()
        fun setTitle(title: Text): Builder {
            this.title = title
            return this
        }

        fun addSlot(index: Int, slot: Slot): Builder {
            if (slotBindings.contains(index)) {
                logger.warn("Adding duplicated slot index to GUI. type=$type, index=$index, slot=$slot")
            }
            slotBindings[index] = slot
            return this
        }

        fun addSlot(x: Int, y: Int, slot: Slot) = this.addSlot(inventoryUtils.xyToIndex(x, y), slot)

        fun build(): GUI {
            return GUI(type, title, slotBindings)
        }
    }

    private inner class GuiContainer(syncId: Int, playerInventory: PlayerInventory) : ScreenHandler(type, syncId) {
        init {
            inventory.onOpen(playerInventory.player)

            // Add slot
            // GUI Inventory slot
            for (i in 0 until inventory.size()) {
                addSlot(slotBindings.getOrElse(i) { Slot(inventory, i, 0, 0) }) // xy only use on client side, ignore it.
            }
            // Player inventory
            // Index 0..8 is HotBar, 9..35 is Inventory
            for (y in 0 until 3) {
                for (x in 0 until 9) {
                    addSlot(Slot(playerInventory, x + y * 9 + 9, 0, 0))
                }
            }
            for (j in 0 until 9) {
                addSlot(Slot(playerInventory, j, 0, 0))
            }

            openListener.forEach { it.invoke(playerInventory.player) }
        }

        override fun onClosed(player: PlayerEntity) {
            super.onClosed(player)
            closeListener.forEach { it.invoke(player) }
        }

        override fun onSlotClick(slot: Int, button: Int, action: SlotActionType, player: PlayerEntity) {
            // Trigger binding
            if (slot in 0 until inventory.size()) {
                inventoryUtils.indexToXY(slot).let { (x, y) ->
                    val event = GUIClickEvent(player as ServerPlayerEntity, x, y, action, inventory.getStack(slot))
                    bindings[slot]?.invoke(event)
                    rangeBindings.filterKeys { (xRange, yRange) -> x in xRange && y in yRange }.values.forEach { it.invoke(event) }
                    if (event.cancel) {
                        if (action == QUICK_CRAFT) endQuickCraft()
                        return
                    }
                }
            }

            // Cancel player change inventory
            if (slot < inventory.size() && slot != -999 && !slotBindings.contains(slot)) {
                if (action == QUICK_CRAFT) endQuickCraft()
                return
            }

            return when (action) {
                PICKUP, SWAP, CLONE, THROW, QUICK_CRAFT -> super.onSlotClick(slot, button, action, player)
                QUICK_MOVE -> {
                    if (slot in 0 until inventory.size() && !slotBindings.contains(slot)) return

                    val inventorySlot = slots[slot]

                    if (inventorySlot.hasStack()) {
                        val slotItemStack = inventorySlot.stack

                        // Move item from GUI to player inventory
                        if (slot < inventory.size() && !insertItem(slotItemStack, playerInventoryRange.first, playerHotBarRange.last, true)) return

                        // Move item from player inventory to GUI
                        if (slot >= inventory.size() && !insertItemToBinding(slotItemStack, false)) return

                        // clean up empty slot
                        if (slotItemStack.isEmpty) {
                            inventorySlot.stack = ItemStack.EMPTY
                        } else {
                            inventorySlot.markDirty()
                        }
                    }

                    return
                }
                PICKUP_ALL -> { // Rewrite PICKUP_ALL only take from allow use slot & player inventory.
                    if (slot < 0) return

                    val cursorItemStack = player.currentScreenHandler.cursorStack
                    val clickSlot = slots[slot]
                    if (!(cursorItemStack.isEmpty || clickSlot.hasStack() && clickSlot.canTakeItems(player))) {
                        loop@ for (tryTime in 0..1) { // First time only take not full stack items
                            val list = (slotBindings.keys.sorted() + (inventory.size() until slots.size)).let { if (button == 0) it else it.reversed() }
                            for (index in list) {
                                if (cursorItemStack.count >= cursorItemStack.maxCount) break@loop

                                val scanSlot = slots[index]
                                // Check slot item
                                if (scanSlot.hasStack() &&
                                    canInsertItemIntoSlot(scanSlot, cursorItemStack, true) &&
                                    scanSlot.canTakeItems(player) &&
                                    canInsertIntoSlot(cursorItemStack, scanSlot)
                                ) {
                                    val selectItemStack = scanSlot.stack
                                    // Only take not full stack in first turn
                                    if (tryTime != 0 || selectItemStack.count != selectItemStack.maxCount) {
                                        val takeItem = scanSlot.takeStackRange(selectItemStack.count, cursorItemStack.maxCount - cursorItemStack.count, player)
                                        cursorItemStack.increment(takeItem.count)
                                    }
                                }
                            }
                        }
                    }

                    this.sendContentUpdates()
                }
            }
        }

        override fun quickMove(player: PlayerEntity, index: Int): ItemStack {
            // Not in used, logic override in onSlotClick
            return ItemStack.EMPTY
        }

        override fun canUse(player: PlayerEntity): Boolean {
            // TODO close GUI
            return true
        }

        fun insertItemToBinding(item: ItemStack, fromLast: Boolean): Boolean {
            val slots = slotBindings.keys.let { if (fromLast) it.sortedDescending() else it.sorted() }
            var inserted = false

            // Merge same item
            if (item.isStackable) {
                for (index in slots) {
                    val slot = slotBindings[index]!!
                    val originItem = slot.stack
                    if (!originItem.isEmpty && slot.canInsert(item) && ItemStack.canCombine(item, originItem)) {
                        val count = originItem.count + item.count
                        if (count <= item.maxCount) {
                            item.count = 0
                            originItem.count = count
                            slot.markDirty()
                            inserted = true
                        } else if (originItem.count < item.maxCount) {
                            item.decrement(item.maxCount - originItem.count)
                            originItem.count = item.maxCount
                            slot.markDirty()
                            inserted = true
                        }
                    }

                    if (item.isEmpty) break
                }
            }

            // Insert to first empty slot
            if (!item.isEmpty) {
                for (index in slots) {
                    val slot = slotBindings[index]!!
                    val originItem = slot.stack
                    if (originItem.isEmpty && slot.canInsert(item)) {
                        if (item.count > slot.maxItemCount) {
                            slot.stack = item.split(slot.maxItemCount)
                        } else {
                            slot.stack = item.split(item.count)
                        }
                        slot.markDirty()
                        inserted = true
                        break
                    }
                }
            }

            return inserted
        }
    }
}
