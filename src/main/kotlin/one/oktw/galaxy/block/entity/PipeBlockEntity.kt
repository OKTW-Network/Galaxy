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

package one.oktw.galaxy.block.entity

import com.google.common.collect.MapMaker
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.block.listener.CustomBlockNeighborUpdateListener
import one.oktw.galaxy.block.pipe.*
import one.oktw.galaxy.block.pipe.PipeSideMode.*
import one.oktw.galaxy.item.CustomItemHelper
import one.oktw.galaxy.item.PipeModelItem
import one.oktw.galaxy.item.PipeModelItem.Companion.PIPE_PORT_EXPORT
import one.oktw.galaxy.item.PipeModelItem.Companion.PIPE_PORT_IMPORT
import one.oktw.galaxy.item.PipeModelItem.Companion.PIPE_PORT_STORAGE
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.util.getOrCreateSubTag
import java.util.*

open class PipeBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener,
    CustomBlockNeighborUpdateListener,
    SidedInventory {
    companion object {
        private val POSITIVE_DIRECTION = Direction.values().filter { it.direction == Direction.AxisDirection.POSITIVE }
    }

    private val side = HashMap<Direction, PipeSide>()
    private val sideEntity = HashMap<Direction, UUID>()
    private val queue = LinkedList<ItemTransferPacket>()
    private val connectedPipe = MapMaker().weakValues().concurrencyLevel(1).makeMap<Direction, PipeBlockEntity>()
    private var showingItemEntity: ItemEntity? = null
    private var redstone = 0
    private var needUpdatePipeConnect = true

    /**
     * Push [ItemTransferPacket] into pipe queue.
     */
    fun pushItem(item: ItemTransferPacket): Boolean {
        return queue.offer(item)
    }

    /**
     * Get pipe I/O mode.
     */
    fun getMode(side: Direction): PipeSideMode {
        return this.side[side]?.mode ?: NONE
    }

    fun getPressure() {
        queue.size
    }

    override fun tick() {
        super.tick()
        if (queue.isEmpty() && showingItemEntity !== null) {
            showingItemEntity?.discard()
            showingItemEntity = null
        }

        if (needUpdatePipeConnect) {
            updatePipeConnect()
            needUpdatePipeConnect = false
        }

        // TODO show item
        // TODO scan next pipe
        // TODO push item to next pipe
    }

    override fun markRemoved() {
        super.markRemoved()
        val world = world as ServerWorld
        sideEntity.values.forEach { world.getEntity(it)?.discard() }
        side.values.mapNotNull {
            when (it.mode) {
                IMPORT -> PIPE_PORT_IMPORT
                EXPORT -> PIPE_PORT_EXPORT
                STORAGE -> PIPE_PORT_STORAGE
                else -> null
            }?.createItemStack()
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        val pipeData = nbt.getCompound("GalaxyData").getCompound("PipeData")

        if (sideEntity.isNotEmpty() && world is ServerWorld) {
            sideEntity.values.forEach { (world as ServerWorld).getEntity(it)?.discard() }
            sideEntity.clear()
        }

        pipeData.getCompound("SideEntity").run { keys.forEach { sideEntity[Direction.valueOf(it)] = getUuid(it) } }

        queue.clear()
        pipeData.getList("queue", NbtType.COMPOUND).mapTo(queue) { ItemTransferPacket.createFromTag(it as NbtCompound) }
    }

    override fun readCopyableData(nbt: NbtCompound) {
        super.readCopyableData(nbt)

        side.clear()
        nbt.getCompound("GalaxyData").getCompound("PipeData").getCompound("Side").run {
            keys.forEach { direction -> PipeSide.createFromNBT(getCompound(direction))?.let { side[Direction.valueOf(direction)] = it } }
        }
    }

    override fun writeNbt(tag: NbtCompound): NbtCompound {
        super.writeNbt(tag)

        tag.getOrCreateSubTag("GalaxyData").getOrCreateSubTag("PipeData").apply {
            put("Queue", queue.mapTo(NbtList()) { it.toTag(NbtCompound()) })
            put("Side", NbtCompound().apply { side.forEach { (direction, side) -> if (side.mode != NONE) put(direction.name, side.writeNBT(NbtCompound())) } })
            put("SideEntity", NbtCompound().apply { sideEntity.forEach { (direction, v) -> putUuid(direction.name, v) } })
        }

        return tag
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val item = player.getStackInHand(hand)
        val direction = hit.side.opposite
        when (item.let(CustomItemHelper::getItem)) {
            PIPE_PORT_EXPORT -> setSideMode(direction, EXPORT)
            PIPE_PORT_IMPORT -> setSideMode(direction, IMPORT)
            PIPE_PORT_STORAGE -> setSideMode(direction, STORAGE)
            Tool.WRENCH -> {
                val dropPos = pos.offset(hit.side)
                when (getMode(direction)) {
                    IMPORT -> PIPE_PORT_IMPORT.createItemStack()
                    EXPORT -> PIPE_PORT_EXPORT.createItemStack()
                    STORAGE -> PIPE_PORT_STORAGE.createItemStack()
                    else -> null
                }?.let {
                    ItemScatterer.spawn(world, dropPos.x.toDouble(), dropPos.y.toDouble(), dropPos.z.toDouble(), it)
                    setSideMode(direction, NONE)
                    return ActionResult.SUCCESS
                }

                return ActionResult.PASS
            }
            else -> return ActionResult.PASS
        }
        if (!player.isCreative) item.decrement(1)

        return ActionResult.SUCCESS
    }

    override fun onNeighborUpdate(direct: Boolean) {
        if (direct) {
            updatePipeConnect()
            needUpdatePipeConnect = true // Also update on next tick
        } else {
            redstone = world!!.getReceivedRedstonePower(pos)
        }
    }

    override fun clear() {
        // TODO
    }

    override fun size(): Int {
        return 60 // queue 54 + side 6
    }

    override fun isEmpty() = false

    override fun getStack(slot: Int): ItemStack {
        return if (slot < 54) {
            queue.getOrNull(slot)?.item ?: ItemStack.EMPTY
        } else {
            when (side[Direction.byId(slot - 54)]?.mode) {
                IMPORT -> PIPE_PORT_IMPORT.createItemStack()
                EXPORT -> PIPE_PORT_EXPORT.createItemStack()
                STORAGE -> PIPE_PORT_STORAGE.createItemStack()
                else -> ItemStack.EMPTY
            }
        }
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return ItemStack.EMPTY
//        return if (slot < 54) {
//            if (slot > queue.size || amount <= 0) ItemStack.EMPTY else {
//                val item = queue.removeAt(slot)
//                val remove = item.item.split(amount)
//                if (item.item.count > 0) queue.push(item)
//                return remove
//            }
//        } else {
//            val direction = Direction.byId(slot - 54)
//            val mode = side.remove(direction)?.mode
//
//            if (mode != null) {
//                val world = world as ServerWorld
//                sideEntity[direction]?.let(world::getEntity)?.discard()
//            }
//
//            when (mode) {
//                IMPORT -> PIPE_PORT_IMPORT.createItemStack()
//                EXPORT -> PIPE_PORT_EXPORT.createItemStack()
//                STORAGE -> PIPE_PORT_STORAGE.createItemStack()
//                else -> ItemStack.EMPTY
//            }
//        }
    }

    override fun removeStack(slot: Int): ItemStack {
        return ItemStack.EMPTY // Not support
//        return if (slot < 54) {
//            if (slot >= queue.size) ItemStack.EMPTY else queue.removeAt(slot).item
//        } else {
//            val direction = Direction.byId(slot - 54)
//            val mode = side.remove(direction)?.mode
//
//            if (mode != null) {
//                val world = world as ServerWorld
//                sideEntity[direction]?.let(world::getEntity)?.discard()
//            }
//
//            when (mode) {
//                IMPORT -> PIPE_PORT_IMPORT.createItemStack()
//                EXPORT -> PIPE_PORT_EXPORT.createItemStack()
//                STORAGE -> PIPE_PORT_STORAGE.createItemStack()
//                else -> ItemStack.EMPTY
//            }
//        }
    }

    override fun setStack(slot: Int, stack: ItemStack?) {
        // Not support
    }

    override fun canPlayerUse(player: PlayerEntity?) = false

    // Not support IO
    override fun getAvailableSlots(side: Direction?) = intArrayOf()

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = false

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = false

    private fun setSideMode(side: Direction, mode: PipeSideMode) {
        if (mode == NONE) {
            this.side.remove(side)?.let {
                // Remove IO entity
                val world = world as ServerWorld
                sideEntity[side]?.let(world::getEntity)?.discard()

                // Reconnect pipe
                updatePipeConnect()
            }
        } else {
            this.side[side] = when (mode) {
                IMPORT -> PipeSideImport()
                EXPORT -> PipeSideExport()
                STORAGE -> PipeSideStorage()
                NONE -> throw IllegalStateException()
            }
            spawnSideEntity(side, mode)
        }
    }

    private fun updatePipeConnect() {
        val world = (world as ServerWorld)
        for (direction in Direction.values()) {
            val connectPipe = world.getBlockEntity(pos.offset(direction.opposite)) as? PipeBlockEntity
            val sideMode = side[direction]?.mode
            if (connectPipe != null && sideMode == null) {
                connectedPipe[direction] = connectPipe

                // Connect pipe
                if (direction in POSITIVE_DIRECTION && connectPipe.getMode(direction.opposite) == NONE) {
                    spawnSideEntity(direction, NONE)
                }
            } else {
                connectedPipe.remove(direction)

                // Disconnect pipe
                if (sideMode == null) {
                    sideEntity.remove(direction)?.let(world::getEntity)?.discard()
                } else if (sideEntity[direction] == null) {
                    spawnSideEntity(direction, sideMode)
                }
            }
        }
    }

    private fun spawnSideEntity(side: Direction, mode: PipeSideMode) {
        val world = world as ServerWorld
        sideEntity[side]?.let(world::getEntity)?.discard()

        val entity = ItemFrameEntity(world, pos, side).apply {
            readCustomDataFromNbt(NbtCompound().also(this::writeCustomDataToNbt).apply { putBoolean("Fixed", true) })
            isInvisible = true
            isInvulnerable = true
            isSilent = true
            addScoreboardTag("PIPE")
            heldItemStack = when (mode) {
                NONE -> PipeModelItem.PIPE_EXTENDED.createItemStack()
                IMPORT -> PIPE_PORT_IMPORT.createItemStack()
                EXPORT -> PIPE_PORT_EXPORT.createItemStack()
                STORAGE -> PIPE_PORT_STORAGE.createItemStack()
            }
        }
        if (world.spawnEntity(entity)) sideEntity[side] = entity.uuid
    }
}
