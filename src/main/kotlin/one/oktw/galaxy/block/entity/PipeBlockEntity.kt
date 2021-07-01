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
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.block.listener.CustomBlockNeighborUpdateListener
import one.oktw.galaxy.block.pipe.*
import one.oktw.galaxy.block.pipe.IOUpdateInfo.IOUpdateAction.ADD
import one.oktw.galaxy.block.pipe.IOUpdateInfo.IOUpdateAction.REMOVE
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

    private val pipeIO = HashMap<Direction, PipeSide>()
    private val sideEntity = HashMap<Direction, UUID>()
    private val queue = LinkedList<ItemTransferPacket>()
    private val connectedPipe = MapMaker().weakValues().concurrencyLevel(1).makeMap<Direction, PipeBlockEntity>()
    private val connectedIO = HashMap<Direction, MutableSet<PipeSide>>()
    private val updateQueue = LinkedList<Pair<IOUpdateInfo, Direction>>()
    private var showingItemEntity: ItemEntity? = null
    private var redstone = 0
    private var needUpdatePipeConnect = true

    /**
     * Push [ItemTransferPacket] into pipe queue.
     */
    fun pushItem(item: ItemTransferPacket): Boolean {
        item.progress = 0
        return queue.offer(item)
    }

    /**
     * Get pipe I/O mode.
     */
    fun getMode(side: Direction): PipeSideMode {
        return this.pipeIO[side]?.mode ?: NONE
    }

    fun getConnectedIO(): MutableSet<PipeSide> {
        return Collections.newSetFromMap<PipeSide>(WeakHashMap()).apply {
            addAll(pipeIO.values)
            connectedIO.values.forEach(this::addAll)
        }
    }

    fun getPressure(): Int {
        return queue.size
    }

    fun sendIOUpdate(from: Direction, info: IOUpdateInfo) {
        if (!info.path.contains(this) && !pipeIO.values.contains(info.side)) updateQueue.add(Pair(info, from))
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

        if (updateQueue.isNotEmpty()) processIOUpdate()

        // TODO show item
        // TODO scan next pipe
        // TODO push item to next pipe
    }

    override fun markRemoved() {
        super.markRemoved()
        val world = world as ServerWorld
        sideEntity.values.forEach { world.getEntity(it)?.discard() }
        queue.clear()
        connectedPipe.clear()
        connectedIO.clear()
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
        pipeData.getList("Queue", NbtType.COMPOUND).mapTo(queue) { ItemTransferPacket.createFromTag(it as NbtCompound) }
    }

    override fun readCopyableData(nbt: NbtCompound) {
        super.readCopyableData(nbt)

        pipeIO.clear()
        nbt.getCompound("GalaxyData").getCompound("PipeData").getCompound("IO").run {
            keys.forEach { direction -> PipeSide.createFromNBT(getCompound(direction))?.let { pipeIO[Direction.valueOf(direction)] = it } }
        }
    }

    override fun writeNbt(tag: NbtCompound): NbtCompound {
        super.writeNbt(tag)

        tag.getOrCreateSubTag("GalaxyData").getOrCreateSubTag("PipeData").apply {
            put("Queue", queue.mapTo(NbtList()) { it.toTag(NbtCompound()) })
            put("IO", NbtCompound().apply { pipeIO.forEach { (k, v) -> if (v.mode != NONE) put(k.name, v.writeNBT(NbtCompound())) } })
            put("SideEntity", NbtCompound().apply { sideEntity.forEach { (k, v) -> putUuid(k.name, v) } })
        }

        return tag
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val item = player.getStackInHand(hand)
        val direction = hit.side
        val customItem = CustomItemHelper.getItem(item)

        if (customItem == Tool.WRENCH) {
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

        if (pipeIO.contains(direction)) return ActionResult.PASS
        when (customItem) {
            PIPE_PORT_EXPORT -> setSideMode(direction, EXPORT)
            PIPE_PORT_IMPORT -> setSideMode(direction, IMPORT)
            PIPE_PORT_STORAGE -> setSideMode(direction, STORAGE)
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
        queue.clear()
        Direction.values().forEach { setSideMode(it, NONE) }
    }

    override fun size(): Int {
        return 60 // queue 54 + side 6
    }

    override fun isEmpty() = false

    override fun getStack(slot: Int): ItemStack {
        return if (slot < 54) {
            queue.getOrNull(slot)?.item ?: ItemStack.EMPTY
        } else {
            when (pipeIO[Direction.byId(slot - 54)]?.mode) {
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
        val world = world as ServerWorld

        if (mode == NONE) {
            this.pipeIO.remove(side)?.let { io ->
                // Remove IO entity
                sideEntity[side]?.let(world::getEntity)?.discard()

                // Reconnect pipe
                updatePipeConnect()

                // Send IO remove to connected pipe.
                val update = IOUpdateInfo(io, REMOVE).apply { path.add(this@PipeBlockEntity) }
                connectedPipe.forEach { (side, pipe) -> pipe.sendIOUpdate(side.opposite, update.copy()) }
            }
        } else {
            val io = when (mode) {
                IMPORT -> PipeSideImport()
                EXPORT -> PipeSideExport()
                STORAGE -> PipeSideStorage()
                NONE -> throw IllegalStateException()
            }
            this.pipeIO[side] = io
            spawnSideEntity(side, mode)

            // Send IO add to connected pipe.
            val update = IOUpdateInfo(io, ADD).apply { path.add(this@PipeBlockEntity) }
            connectedPipe.forEach { (side, pipe) -> pipe.sendIOUpdate(side.opposite, update.copy()) }
        }
    }

    private fun updatePipeConnect() {
        val world = (world as ServerWorld)
        val newPipe = ArrayList<Direction>()
        for (direction in Direction.values()) {
            val connectPipe = world.getBlockEntity(pos.offset(direction)) as? PipeBlockEntity
            val sideMode = pipeIO[direction]?.mode
            if (connectPipe != null && sideMode == null && connectPipe.getMode(direction.opposite) == NONE) {
                if (connectPipe != connectedPipe[direction]) {
                    connectedPipe[direction] = connectPipe
                    newPipe += direction
                }

                // Connect pipe
                if (direction in POSITIVE_DIRECTION && sideEntity[direction] == null) spawnSideEntity(direction, NONE)
            } else {
                connectedPipe.remove(direction)?.let { connectedIO.remove(direction) }?.forEach { io ->
                    val update = IOUpdateInfo(io, REMOVE).apply { path.add(this@PipeBlockEntity) }
                    connectedPipe.forEach { (side, pipe) -> pipe.sendIOUpdate(side.opposite, update.copy()) }
                }

                // Disconnect pipe
                if (sideMode == null) {
                    sideEntity.remove(direction)?.let(world::getEntity)?.discard()
                } else if (sideEntity[direction] == null) { // TODO move check and respawn IO entity to tick
                    spawnSideEntity(direction, sideMode)
                }
            }
        }

        // Update new pipe IO info
        newPipe.forEach(::getIOInfo)
    }

    private fun getIOInfo(from: Direction) {
        val pipe = connectedPipe[from] ?: return
        val list = pipe.getConnectedIO()

        connectedIO[from] = list

        list.forEach {
            val update = IOUpdateInfo(it, ADD).apply {
                path.add(pipe)
                path.add(this@PipeBlockEntity)
            }
            connectedPipe.forEach { (side, pipe) -> if (side != from) pipe.sendIOUpdate(side.opposite, update.copy()) }
        }
    }

    private fun processIOUpdate() {
        updateQueue.forEach { (info, side) ->
            val ioList = connectedIO.getOrPut(side) { Collections.newSetFromMap(WeakHashMap()) }

            when (info.action) {
                ADD -> ioList.add(info.side)
                REMOVE -> ioList.remove(info.side)
            }

            info.path.add(this)
            connectedPipe.forEach { (side, pipe) -> pipe.sendIOUpdate(side.opposite, info.copy()) }
        }

        updateQueue.clear()
    }

    private fun spawnSideEntity(side: Direction, mode: PipeSideMode) {
        val world = world as ServerWorld
        sideEntity[side]?.let(world::getEntity)?.discard()

        val entity = ItemFrameEntity(world, pos, side.opposite).apply {
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
