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

package one.oktw.galaxy.gui

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.inventory.ClickType.*
import net.minecraft.world.inventory.MenuType.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.gui.utils.InventoryEditor
import one.oktw.galaxy.gui.utils.InventoryUtils
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min

@Suppress("unused", "MemberVisibilityCanBePrivate")
class GUI private constructor(
    private val type: MenuType<out AbstractContainerMenu>,
    private val title: Component,
    private val slotBindings: HashMap<Int, Slot>,
    private val skipPick: HashSet<Int>,
    private val blockEntity: BlockEntity? = null
) :
    MenuProvider {
    companion object {
        val GUI_FONT_SHIFT_CHEST_START: Component = Component.literal("<")
            .withStyle { it.withFont(FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/shift"))) }
        val GUI_FONT_SHIFT_CHEST_END: Component = Component.literal(";>")
            .withStyle { it.withFont(FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/shift"))) }
        val GUI_FONT_SHIFT_ANVIL_START: Component = Component.literal("⟨")
            .withStyle { it.withFont(FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/shift"))) }
        val GUI_FONT_SHIFT_ANVIL_END: Component = Component.literal(";⟩")
            .withStyle { it.withFont(FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/shift"))) }
    }

    private val inventory = when (type) {
        GENERIC_9x1, GENERIC_3x3 -> SimpleContainer(9)
        GENERIC_9x2 -> SimpleContainer(9 * 2)
        GENERIC_9x3 -> SimpleContainer(9 * 3)
        GENERIC_9x4 -> SimpleContainer(9 * 4)
        GENERIC_9x5 -> SimpleContainer(9 * 5)
        GENERIC_9x6 -> SimpleContainer(9 * 6)
        HOPPER -> SimpleContainer(5)
        ANVIL -> SimpleContainer(3)
        else -> throw IllegalArgumentException("Unsupported container type: $type")
    }
    private val playerInventoryRange = inventory.containerSize until inventory.containerSize + 3 * 9
    private val playerHotBarRange = playerInventoryRange.last + 1..playerInventoryRange.last + 1 + 9
    private val bindings = ConcurrentHashMap<Int, GUIClickEvent.() -> Any>()
    private val rangeBindings = ConcurrentHashMap<Pair<IntRange, IntRange>, GUIClickEvent.() -> Any>()
    private val inventoryUtils = InventoryUtils(type)
    private val openListener = ConcurrentHashMap.newKeySet<(Player) -> Any>()
    private val closeListener = ConcurrentHashMap.newKeySet<(Player) -> Any>()
    private val updateListener = ConcurrentHashMap.newKeySet<() -> Any>()
    var inputText: String = ""
        private set

    override fun getDisplayName() = title

    override fun createMenu(syncId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        if ((blockEntity as? Container)?.stillValid(player) == false) return null
        return GuiContainer(syncId, playerInventory)
    }

    fun getItem(x: Int, y: Int) = getItem(inventoryUtils.xyToIndex(x, y))

    fun getItem(index: Int): ItemStack {
        if (index !in 0 until inventory.containerSize) throw IndexOutOfBoundsException()

        return inventory.getItem(index)
    }

    fun addBinding(index: Int, function: GUIClickEvent.() -> Any) {
        if (index !in 0 until inventory.containerSize) throw IndexOutOfBoundsException("Binding index out of inventory range")

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
        main!!.launch(server.asCoroutineDispatcher(), if (server.isSameThread) CoroutineStart.UNDISPATCHED else CoroutineStart.DEFAULT) {
            block.invoke(InventoryEditor(type, inventory))
            inventory.setChanged()
        }
    }

    fun onOpen(block: (Player) -> Any) {
        openListener += block
    }

    fun onClose(block: (Player) -> Any) {
        closeListener += block
    }

    fun onUpdate(block: () -> Any) {
        updateListener += block
    }

    private fun checkRange(x: Int, y: Int) = inventoryUtils.xyToIndex(x, y) in 0 until inventory.containerSize

    class Builder(private val type: MenuType<out AbstractContainerMenu>) {
        private val logger = LogManager.getLogger()
        private val inventoryUtils = InventoryUtils(type)
        private var title: Component = Component.empty()
        private var background: Optional<MutableComponent> = Optional.empty()
        private val slotBindings = HashMap<Int, Slot>()
        private val skipPick = HashSet<Int>()
        private var blockEntity: BlockEntity? = null

        fun setTitle(title: Component): Builder {
            this.title = title
            return this
        }

        fun setBackground(char: String, font: ResourceLocation): Builder {
            val background = Component.empty()
                .append(if (type == ANVIL) GUI_FONT_SHIFT_ANVIL_START else GUI_FONT_SHIFT_CHEST_START)
                .append(Component.literal(char).withStyle { it.withFont(FontDescription.Resource(font)).withColor(ChatFormatting.WHITE) })
                .append(if (type == ANVIL) GUI_FONT_SHIFT_ANVIL_END else GUI_FONT_SHIFT_CHEST_END)

            this.background = Optional.of(background)
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

        /**
         * Set Slot to be skipped during PICK_ALL scan
         */
        fun setSkipPick(index: Int): Builder {
            skipPick += index
            return this
        }

        /**
         * Set Slot to be skipped during PICK_ALL scan
         */
        fun setSkipPick(x: Int, y: Int) = this.setSkipPick(inventoryUtils.xyToIndex(x, y))

        fun blockEntity(entity: BlockEntity): Builder {
            this.blockEntity = entity
            return this
        }

        fun build(): GUI {
            val title = if (background.isPresent) background.get().append(title) else title
            return GUI(type, title, slotBindings, skipPick, blockEntity)
        }
    }

    inner class GuiContainer(syncId: Int, playerInventory: Inventory) : AbstractContainerMenu(type, syncId), ContainerListener {
        init {
            this.addSlotListener(this)
            inventory.startOpen(playerInventory.player)

            // Add slot
            // GUI Inventory slot
            for (i in 0 until inventory.containerSize) {
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

        override fun removed(player: Player) {
            super.removed(player)
            closeListener.forEach { it.invoke(player) }
        }

        override fun slotChanged(handler: AbstractContainerMenu, slotId: Int, stack: ItemStack) {
            updateListener.forEach { it.invoke() }
        }

        override fun dataChanged(handler: AbstractContainerMenu, property: Int, value: Int) {
            // Unused
        }

        override fun clicked(slot: Int, button: Int, action: ClickType, player: Player) {
            // Trigger binding
            if (slot in 0 until inventory.containerSize) {
                inventoryUtils.indexToXY(slot).let { (x, y) ->
                    val event = GUIClickEvent(player as ServerPlayer, x, y, action, inventory.getItem(slot))
                    bindings[slot]?.invoke(event)
                    rangeBindings.filterKeys { (xRange, yRange) -> x in xRange && y in yRange }.values.forEach { it.invoke(event) }
                    if (event.cancel) {
                        if (action == QUICK_CRAFT) resetQuickCraft()
                        return
                    }
                }
            }

            // Cancel player change inventory
            if (slot < inventory.containerSize && slot != -999 && !slotBindings.contains(slot)) {
                if (action == QUICK_CRAFT) resetQuickCraft()
                if (action == CLONE && player.isCreative) super.clicked(slot, button, action, player)
                return
            }

            when (action) {
                PICKUP, SWAP, CLONE, QUICK_CRAFT -> super.clicked(slot, button, action, player)
                THROW -> {
                    if (!player.canDropItems() || slot < 0) return
                    val inventorySlot = slots[slot]
                    val takeCount = if (button == 0) 1 else inventorySlot.item.count

                    // Limit max take count
                    val maxStack = inventorySlot.item.maxStackSize
                    var stack = inventorySlot.safeTake(takeCount, Int.MAX_VALUE, player)
                    var count = stack.count
                    player.drop(stack, true)
                    if (button == 1) {
                        while (count + min(takeCount, inventorySlot.item.count) <= maxStack && ItemStack.isSameItem(inventorySlot.item, stack)) {
                            if (!player.canDropItems()) return

                            stack = inventorySlot.safeTake(takeCount, Int.MAX_VALUE, player)
                            if (stack.isEmpty) break
                            count += stack.count
                            player.drop(stack, true)
                        }
                    }
                }

                QUICK_MOVE -> {
                    if (slot in 0 until inventory.containerSize && !slotBindings.contains(slot)) return

                    val inventorySlot = slots[slot]

                    // Limit max take count
                    val maxStack = inventorySlot.item.maxStackSize
                    var count = 0
                    while (count + inventorySlot.item.count <= maxStack && inventorySlot.hasItem() && inventorySlot.mayPickup(player)) {
                        val slotItemStack = inventorySlot.item
                        val origCount = slotItemStack.count

                        // Move item from GUI to player inventory
                        if (slot < inventory.containerSize && !moveItemStackTo(slotItemStack, playerInventoryRange.first, playerHotBarRange.last, true)) return

                        // Move item from player inventory to GUI
                        if (slot >= inventory.containerSize && !insertItemToBinding(slotItemStack, false)) return

                        // clean up empty slot
                        if (slotItemStack.isEmpty) {
                            inventorySlot.setByPlayer(ItemStack.EMPTY)
                        } else {
                            inventorySlot.setChanged()
                        }

                        count += origCount - slotItemStack.count
                        inventorySlot.onTake(player, slotItemStack)
                    }
                }

                PICKUP_ALL -> { // Rewrite PICKUP_ALL only take from allow use slot & player inventory.
                    if (slot < 0) return

                    val cursorItemStack = player.containerMenu.carried
                    val clickSlot = slots[slot]
                    if (!(cursorItemStack.isEmpty || clickSlot.hasItem() && clickSlot.mayPickup(player))) {
                        loop@ for (tryTime in 0..1) { // First time only take not full stack items
                            val list = (slotBindings.keys.sorted() + (inventory.containerSize until slots.size)).let { if (button == 0) it else it.reversed() }
                            for (index in list) {
                                if (cursorItemStack.count >= cursorItemStack.maxStackSize) break@loop
                                if (index in skipPick) continue

                                val scanSlot = slots[index]
                                // Check slot item
                                if (scanSlot.hasItem() &&
                                    canItemQuickReplace(scanSlot, cursorItemStack, true) &&
                                    scanSlot.mayPickup(player) &&
                                    canTakeItemForPickAll(cursorItemStack, scanSlot)
                                ) {
                                    val selectItemStack = scanSlot.item
                                    // Only take not full stack in first turn
                                    if (tryTime != 0 || selectItemStack.count != selectItemStack.maxStackSize) {
                                        val takeItem = scanSlot.safeTake(selectItemStack.count, cursorItemStack.maxStackSize - cursorItemStack.count, player)
                                        cursorItemStack.grow(takeItem.count)
                                    }
                                }
                            }
                        }
                    }

                    this.broadcastChanges()
                }
            }
        }

        override fun quickMoveStack(player: Player, index: Int): ItemStack {
            // Not in used, logic override in onSlotClick
            return ItemStack.EMPTY
        }

        override fun stillValid(player: Player): Boolean {
            return (blockEntity as? Container)?.stillValid(player) ?: true
        }

        fun insertItemToBinding(item: ItemStack, fromLast: Boolean): Boolean {
            val slots = slotBindings.keys.let { if (fromLast) it.sortedDescending() else it.sorted() }
            var inserted = false

            // Merge same item
            if (item.isStackable) {
                for (index in slots) {
                    val slot = slotBindings[index]!!
                    val originItem = slot.item
                    if (!originItem.isEmpty && slot.mayPlace(item) && ItemStack.isSameItemSameComponents(item, originItem)) {
                        val count = originItem.count + item.count
                        if (count <= item.maxStackSize) {
                            item.count = 0
                            originItem.count = count
                            slot.setChanged()
                            inserted = true
                        } else if (originItem.count < item.maxStackSize) {
                            item.shrink(item.maxStackSize - originItem.count)
                            originItem.count = item.maxStackSize
                            slot.setChanged()
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
                    val originItem = slot.item
                    if (originItem.isEmpty && slot.mayPlace(item)) {
                        if (item.count > slot.maxStackSize) {
                            slot.setByPlayer(item.split(slot.maxStackSize))
                        } else {
                            slot.setByPlayer(item.split(item.count))
                        }
                        slot.setChanged()
                        inserted = true
                        break
                    }
                }
            }

            return inserted
        }

        fun updateInputText(input: String) {
            inputText = input
            broadcastFullState()
        }
    }
}
