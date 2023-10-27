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

package one.oktw.galaxy.block.entity

import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.HoeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.block.listener.CustomBlockTickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.util.HarvestUtil

class HarvestBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) :
    ModelCustomBlockEntity(type, pos, modelItem, facing = Direction.NORTH),
    CustomBlockClickListener, SidedInventory, CustomBlockTickListener {
    companion object {
        private val ALL_SLOT = intArrayOf(0, 1, 2, 3)
    }

    override val allowedFacing = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    private val inventory = DefaultedList.ofSize(4, ItemStack.EMPTY)
    private val gui = GUI.Builder(ScreenHandlerType.GENERIC_9X3).setTitle(Text.translatable("block.HARVEST")).apply {
        var i = 0
        addSlot(4, 0, object : Slot(this@HarvestBlockEntity, i++, 0, 0) { // Tool
            override fun canInsert(item: ItemStack) = isHoe(item)
        })
        for (x in 3..5) addSlot(x, 2, object : Slot(this@HarvestBlockEntity, i++, 0, 0) { // Output
            override fun canInsert(stack: ItemStack) = false
        })
    }.build().apply {
        editInventory {
            // Fill empty
            fillAll(Gui.MAIN_FIELD.createItemStack())
        }
    }
    private var progress = 0

    override fun tick() {
        super.tick()

        // Only work have hoe and empty slot >= 2
        val tool = inventory[0]
        if (!isHoe(tool) || inventory.count { it.isEmpty } < 2) return

        val world = world as? ServerWorld ?: return

        val range = 4 // TODO range upgrade
        val diameter = range * 2 + 1
        if (progress >= diameter * diameter) progress = 0
        val blockPos = pos.offset(facing, (progress / diameter) + 1).offset(facing!!.rotateYClockwise(), (progress % diameter) - range)
        progress++
        val blockState = world.getBlockState(blockPos)

        if (HarvestUtil.isMature(world, blockPos, world.getBlockState(blockPos))) {
            val block = blockState.block
            val ageProperties = when (block) {
                Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES -> CropBlock.AGE
                Blocks.BEETROOTS -> BeetrootsBlock.AGE
                Blocks.COCOA -> CocoaBlock.AGE
                Blocks.NETHER_WART -> NetherWartBlock.AGE
                Blocks.PUMPKIN, Blocks.MELON -> null
                else -> return
            }
            world.breakBlock(blockPos, false)
            val drop = Block.getDroppedStacks(blockState, world, blockPos, world.getBlockEntity(blockPos), null, tool)
            for (item in drop) {
                for (slot in 1..3) {
                    val originItem = getStack(slot)
                    if (originItem.isEmpty) {
                        setStack(slot, item)
                        break
                    } else if (originItem.count < originItem.maxCount && ItemStack.canCombine(originItem, item)) {
                        val count = item.count.coerceAtMost(originItem.maxCount - originItem.count)
                        item.decrement(count)
                        originItem.increment(count)
                        if (item.isEmpty) break
                    }
                }
            }
            if (tool.damage(1, world.random, null)) {
                tool.decrement(1)
                tool.damage = 0
            }

            if (block != Blocks.PUMPKIN && block != Blocks.MELON) {
                world.setBlockState(blockPos, blockState.with(ageProperties, 0))
                world.updateNeighbors(blockPos, block)
            }
        }
    }

    override fun readCopyableData(nbt: NbtCompound) {
        Inventories.readNbt(nbt, inventory)
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        GUISBackStackManager.openGUI(player as ServerPlayerEntity, gui)
        return ActionResult.SUCCESS
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
        return if (world!!.getBlockEntity(pos) !== this) {
            false
        } else player.squaredDistanceTo(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5) <= 64.0
    }

    override fun getAvailableSlots(side: Direction): IntArray {
        return ALL_SLOT
    }

    override fun canInsert(slot: Int, item: ItemStack, dir: Direction?): Boolean {
        return slot == 0 && isHoe(item)
    }

    override fun canExtract(slot: Int, item: ItemStack, dir: Direction): Boolean {
        return slot in 1..3
    }

    private fun isHoe(item: ItemStack) = item.item is HoeItem
}
