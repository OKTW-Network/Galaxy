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
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.HoeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.BlockHitResult
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.block.listener.CustomBlockTickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Misc
import one.oktw.galaxy.item.Upgrade
import one.oktw.galaxy.util.HarvestUtil

class HarvestBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) :
    ModelCustomBlockEntity(type, pos, modelItem, facing = Direction.NORTH), CustomBlockClickListener, WorldlyContainer, CustomBlockTickListener {
    companion object {
        private val TOOL_SLOT = 0..0
        private val UPGRADE_SLOT = 1..1
        private val STORAGE_SLOT = 2..6
        private val ACCESS_SLOT = (TOOL_SLOT + STORAGE_SLOT).toIntArray()
    }

    override val allowedFacing = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    private val inventory = NonNullList.withSize(7, ItemStack.EMPTY)
    private var rangeCache = 0

    private val gui = GUI.Builder(MenuType.GENERIC_9x3).setTitle(Component.translatable("block.HARVEST"))
        .setBackground("A", Identifier.fromNamespaceAndPath("galaxy", "gui_font/container_layout/harvest")).blockEntity(this).apply {
            addSlot(4, 0, object : Slot(this@HarvestBlockEntity, TOOL_SLOT.first, 0, 0) { // Tool
                override fun mayPlace(item: ItemStack) = isHoe(item)
            })
            addSlot(8, 0, object : Slot(this@HarvestBlockEntity, UPGRADE_SLOT.first, 0, 0) { // Upgrade
                override fun mayPlace(item: ItemStack) = Upgrade.getFromItem(item)?.type == Upgrade.Type.RANGE
                override fun setChanged() {
                    rangeCache = 0
                    super.setChanged()
                }
            })
            var i = STORAGE_SLOT.first
            for (x in 2..6) addSlot(x, 2, object : Slot(this@HarvestBlockEntity, i++, 0, 0) { // Output
                override fun mayPlace(stack: ItemStack) = false
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

        val world = level as? ServerLevel ?: return

        val range = getRange()
        val diameter = range * 2 + 1
        if (progress >= diameter * diameter) progress = 0
        val blockPos = worldPosition.relative(facing!!, (progress / diameter) + 1).relative(facing!!.clockWise, (progress % diameter) - range)
        progress++
        val blockState = world.getBlockState(blockPos)

        if (HarvestUtil.isMature(world, blockPos, blockState)) {
            val ageProperties = HarvestUtil.getAgeProp(blockState.block)
            world.destroyBlock(blockPos, false)
            val drop = Block.getDrops(blockState, world, blockPos, null, null, tool)
            for (item in drop) {
                for (slot in STORAGE_SLOT) {
                    val originItem = getItem(slot)
                    if (originItem.isEmpty) {
                        setItem(slot, item)
                        break
                    } else if (originItem.count < originItem.maxStackSize && ItemStack.isSameItemSameComponents(originItem, item)) {
                        val count = item.count.coerceAtMost(originItem.maxStackSize - originItem.count)
                        item.shrink(count)
                        originItem.grow(count)
                        if (item.isEmpty) break
                    }
                }
            }
            tool.hurtAndBreak(1, world, null) {
                tool.shrink(1)
                tool.damageValue = 0
            }

            if (ageProperties != null) {
                world.setBlockAndUpdate(blockPos, blockState.setValue(ageProperties, 0))
                world.updateNeighborsAt(blockPos, blockState.block)
            }
        }
    }

    override fun readCopyableData(view: ValueInput) {
        super.readCopyableData(view)
        ContainerHelper.loadAllItems(view, inventory)
    }

    override fun saveAdditional(view: ValueOutput) {
        super.saveAdditional(view)
        ContainerHelper.saveAllItems(view, inventory)
    }

    override fun collectImplicitComponents(builder: DataComponentMap.Builder) {
        super.collectImplicitComponents(builder)
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory))
    }

    override fun applyImplicitComponents(components: DataComponentGetter) {
        super.applyImplicitComponents(components)
        components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(inventory)
    }

    override fun removeComponentsFromTag(view: ValueOutput) {
        super.removeComponentsFromTag(view)
        view.discard("Items")
    }

    override fun onClick(player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        GUISBackStackManager.openGUI(player as ServerPlayer, gui)
        return InteractionResult.SUCCESS_SERVER
    }

    override fun clearContent() {
        inventory.clear()
    }

    override fun getContainerSize() = inventory.size

    override fun isEmpty() = inventory.isEmpty()

    override fun getItem(slot: Int) = inventory[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(inventory, slot, amount)
        if (!itemStack.isEmpty) {
            this.setChanged()
        }
        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(inventory, slot)

    override fun setItem(slot: Int, stack: ItemStack) {
        inventory[slot] = stack
        if (stack.count > this.maxStackSize) {
            stack.count = this.maxStackSize
        }
        this.setChanged()
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun getSlotsForFace(side: Direction): IntArray {
        return ACCESS_SLOT
    }

    override fun canPlaceItemThroughFace(slot: Int, item: ItemStack, dir: Direction?): Boolean {
        return slot in TOOL_SLOT && isHoe(item)
    }

    override fun canTakeItemThroughFace(slot: Int, item: ItemStack, dir: Direction): Boolean {
        return slot in STORAGE_SLOT
    }

    private fun isHoe(item: ItemStack) = item.item is HoeItem

    private fun getRange(): Int {
        if (rangeCache != 0) return rangeCache

        val base = 4
        for (slot in UPGRADE_SLOT) {
            val item = inventory[slot]
            if (item.isEmpty) continue
            val upgrade = Upgrade.getFromItem(item) ?: continue
            if (upgrade.type == Upgrade.Type.RANGE) {
                rangeCache = base + upgrade.level
                return rangeCache
            }
        }

        rangeCache = base
        return rangeCache
    }
}
