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

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.component.ComponentMap
import net.minecraft.component.ComponentsAccess
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ContainerComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.HoeItem
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.block.listener.CustomBlockTickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Misc
import one.oktw.galaxy.item.Upgrade
import one.oktw.galaxy.util.HarvestUtil

class HarvestBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) :
    ModelCustomBlockEntity(type, pos, modelItem, facing = Direction.NORTH), CustomBlockClickListener, SidedInventory, CustomBlockTickListener {
    companion object {
        private val TOOL_SLOT = 0..0
        private val UPGRADE_SLOT = 1..1
        private val STORAGE_SLOT = 2..6
        private val ACCESS_SLOT = (TOOL_SLOT + STORAGE_SLOT).toIntArray()
    }

    override val allowedFacing = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    private val inventory = DefaultedList.ofSize(7, ItemStack.EMPTY)

    private val gui = GUI.Builder(ScreenHandlerType.GENERIC_9X3).setTitle(Text.translatable("block.HARVEST"))
        .setBackground("A", Identifier.of("galaxy", "gui_font/container_layout/harvest")).blockEntity(this).apply {
            addSlot(4, 0, object : Slot(this@HarvestBlockEntity, TOOL_SLOT.first, 0, 0) { // Tool
                override fun canInsert(item: ItemStack) = isHoe(item)
            })
            addSlot(8, 0, object : Slot(this@HarvestBlockEntity, UPGRADE_SLOT.first, 0, 0) { // Upgrade
                override fun canInsert(item: ItemStack) = Upgrade.getFromItem(item)?.type == Upgrade.Type.RANGE
            })
            var i = STORAGE_SLOT.first
            for (x in 2..6) addSlot(x, 2, object : Slot(this@HarvestBlockEntity, i++, 0, 0) { // Output
                override fun canInsert(stack: ItemStack) = false
            })
        }.build().apply {
            editInventory {
                // Fill empty
                fillAll(Misc.PLACEHOLDER.createItemStack())
            }
        }
    private var progress = 0

    override fun tick() {
        super.tick()

        // Only work have hoe and empty slot >= 2
        val tool = inventory[TOOL_SLOT.first]
        if (!isHoe(tool) || inventory.slice(STORAGE_SLOT).count { it.isEmpty } < 2) return

        val world = world as? ServerWorld ?: return

        val range = getRange()
        val diameter = range * 2 + 1
        if (progress >= diameter * diameter) progress = 0
        val blockPos = pos.offset(facing, (progress / diameter) + 1).offset(facing!!.rotateYClockwise(), (progress % diameter) - range)
        progress++
        val blockState = world.getBlockState(blockPos)

        if (HarvestUtil.isMature(world, blockPos, blockState)) {
            val ageProperties = HarvestUtil.getAgeProp(blockState.block)
            world.breakBlock(blockPos, false)
            val drop = Block.getDroppedStacks(blockState, world, blockPos, null, null, tool)
            for (item in drop) {
                for (slot in STORAGE_SLOT) {
                    val originItem = getStack(slot)
                    if (originItem.isEmpty) {
                        setStack(slot, item)
                        break
                    } else if (originItem.count < originItem.maxCount && ItemStack.areItemsAndComponentsEqual(originItem, item)) {
                        val count = item.count.coerceAtMost(originItem.maxCount - originItem.count)
                        item.decrement(count)
                        originItem.increment(count)
                        if (item.isEmpty) break
                    }
                }
            }
            tool.damage(1, world, null) {
                tool.decrement(1)
                tool.damage = 0
            }

            if (ageProperties != null) {
                world.setBlockState(blockPos, blockState.with(ageProperties, 0))
                world.updateNeighbors(blockPos, blockState.block)
            }
        }
    }

    override fun readCopyableData(view: ReadView) {
        super.readCopyableData(view)
        Inventories.readData(view, inventory)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory)
    }

    override fun addComponents(builder: ComponentMap.Builder) {
        super.addComponents(builder)
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory))
    }

    override fun readComponents(components: ComponentsAccess) {
        super.readComponents(components)
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(inventory)
    }

    override fun removeFromCopiedStackData(view: WriteView) {
        super.removeFromCopiedStackData(view)
        view.remove("Items")
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

    override fun getAvailableSlots(side: Direction): IntArray {
        return ACCESS_SLOT
    }

    override fun canInsert(slot: Int, item: ItemStack, dir: Direction?): Boolean {
        return slot in TOOL_SLOT && isHoe(item)
    }

    override fun canExtract(slot: Int, item: ItemStack, dir: Direction): Boolean {
        return slot in STORAGE_SLOT
    }

    private fun isHoe(item: ItemStack) = item.item is HoeItem

    private fun getRange(): Int {
        val base = 4
        for (slot in UPGRADE_SLOT) {
            val item = inventory[slot]
            if (item.isEmpty) continue
            val upgrade = Upgrade.getFromItem(item) ?: continue
            if (upgrade.type == Upgrade.Type.RANGE) return base + upgrade.level
        }

        return base
    }
}
