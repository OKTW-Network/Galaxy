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
import net.minecraft.block.Block
import net.minecraft.block.Blocks
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
import net.minecraft.world.TickPriority
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
import kotlin.math.min

open class PipeBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener,
    CustomBlockNeighborUpdateListener,
    SidedInventory {
    companion object {
        private val POSITIVE_DIRECTION = Direction.values().filter { it.direction == Direction.AxisDirection.POSITIVE }
    }

    private val pipeIO = EnumMap<Direction, PipeSide>(Direction::class.java)
    private val sideEntity = EnumMap<Direction, UUID>(Direction::class.java)
    private val queue = LinkedList<ItemTransferPacket>()
    private val connectedPipe = MapMaker().weakValues().concurrencyLevel(1).makeMap<Direction, PipeBlockEntity>()
    private val connectedIO = WeakHashMap<PipeSide, EnumMap<Direction, Int>>()
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

    fun getConnectedIO(exclude: Direction? = null): Map<PipeSide, Int> {
        return HashMap<PipeSide, Int>(pipeIO.size + connectedIO.size).apply {
            pipeIO.forEach { (side, io) -> if (side != exclude) put(io, 0) }
            connectedIO.forEach { (io, info) ->
                info.minOfOrNull { if (it.key == exclude) Int.MAX_VALUE else it.value }?.let { if (it != Int.MAX_VALUE) put(io, it) }
            }
        }
    }

    fun getPressure(): Int {
        return queue.size
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

        if (queue.isNotEmpty()) this.markDirty()

        // Tick IO CoolDown and cleanup removed IO
        pipeIO.entries.removeIf { (_, io) -> io.removed || io.tick() != Unit }

        // Output
        queue.removeIf { packet -> pipeIO.values.any { it.output(packet.item).isEmpty } }

        val transferBuffer = queue.filterTo(ArrayList()) { packet -> min(packet.progress + 1, 20).also { packet.progress = it } == 20 }
        if (transferBuffer.isNotEmpty()) {
            val pushed = ArrayList<ItemTransferPacket>(transferBuffer.size)
            val exportIO = connectedIO.filterKeys { it is PipeSideExport && !it.isFull() }
            val sortedPipes = connectedPipe.map { (k, v) -> Pair(k, v) }.sortedBy { (side, pipe) ->
                var distance = Int.MAX_VALUE
                exportIO.forEach { (_, info) ->
                    if (info[side] ?: Int.MAX_VALUE < distance) info[side]?.let { distance = it }
                    if (distance == 1) return@sortedBy pipe.getPressure() * 1000 + distance
                }

                if (distance == Int.MAX_VALUE) Int.MAX_VALUE else pipe.getPressure() * 1000 + distance
            }
            var selfPressure = getPressure()

            transferBuffer.forEach transfer@{ packet ->
                packet.progress = 0

                // Push request item
                if (packet.destination != null) {
                    connectedIO.filterKeys { it.id == packet.destination }.values
                        .flatMapTo(HashSet()) { it.keys.mapNotNull(connectedPipe::get) }
                        .sortedBy { it.getPressure() }
                        .forEach {
                            if (it.pushItem(packet)) {
                                pushed.add(packet)
                                selfPressure--
                                return@transfer
                            }
                        }
                }

                // Low pressure and have export first
                sortedPipes.forEach { (side, pipe) ->
                    if (pipe.getPressure() < selfPressure &&
                        exportIO.any { (io, info) -> info.contains(side) && (io as PipeSideExport).canExport(packet.item) } &&
                        pipe.pushItem(packet)
                    ) {
                        pushed.add(packet)
                        selfPressure--
                        return@transfer
                    }
                }

                // Just push to low pressure pipe
                sortedPipes.forEach { (_, pipe) ->
                    if (pipe.getPressure() < selfPressure && pipe.pushItem(packet)) {
                        pushed.add(packet)
                        selfPressure--
                        return@transfer
                    }
                }
            }

            queue.removeAll(pushed)
        }

        // Input
        if (queue.size < 54) {
            for (io in pipeIO.values) {
                io.input().let { if (!it.isEmpty) queue.add(ItemTransferPacket(io.id, it)) }
                if (queue.size >= 54) break
            }
        }

        if (queue.isNotEmpty()) this.markDirty()

        // TODO show item
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
            keys.forEach { side ->
                val direction = Direction.valueOf(side)
                PipeSide.createFromNBT(this@PipeBlockEntity, direction, getCompound(side))?.let { pipeIO[direction] = it }
            }
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

    override fun scheduledTick() {
        updateIOInfo()
    }

    override fun clear() {
        queue.clear()
        Direction.values().forEach { setSideMode(it, NONE) }
    }

    override fun size(): Int {
        return 60 // queue 54 + io 6
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

    override fun setStack(slot: Int, stack: ItemStack) {
        // Not support
    }

    override fun canPlayerUse(player: PlayerEntity?) = false

    // Not support IO
    override fun getAvailableSlots(side: Direction?) = intArrayOf()

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = false

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = false

    private fun setSideMode(side: Direction, mode: PipeSideMode) {
        val world = world as ServerWorld

        if (pipeIO[side]?.mode == mode) return

        if (mode == NONE) {
            this.pipeIO.remove(side)?.let {
                it.remove()
                // Remove IO entity
                sideEntity.remove(side)?.let(world::getEntity)?.discard()

                // Reconnect pipe
                updatePipeConnect()
                cachedState.updateNeighbors(world, pos, Block.NOTIFY_LISTENERS)
            }
        } else {
            val io = when (mode) {
                IMPORT -> PipeSideImport(this, side)
                EXPORT -> PipeSideExport(this, side)
                STORAGE -> PipeSideStorage(this, side)
                NONE -> throw IllegalStateException()
            }
            this.pipeIO[side] = io
            spawnSideEntity(side, mode)
        }

        this.markDirty()

        // Update connected pipes
        connectedPipe.keys.forEach { world.blockTickScheduler.schedule(pos.offset(it), Blocks.BARRIER, 1, TickPriority.EXTREMELY_HIGH) }
    }

    private fun updatePipeConnect() {
        val world = (world as ServerWorld)
        for (direction in Direction.values()) {
            val connectPipe = world.getBlockEntity(pos.offset(direction)) as? PipeBlockEntity
            val sideMode = pipeIO[direction]?.mode
            if (connectPipe != null && sideMode == null && connectPipe.getMode(direction.opposite) == NONE) {
                if (connectPipe != connectedPipe[direction]) {
                    connectedPipe[direction] = connectPipe
                    updateIOInfo(direction)
                }

                // Connect pipe
                if (direction in POSITIVE_DIRECTION && sideEntity[direction] == null) spawnSideEntity(direction, NONE)
            } else {
                connectedPipe.remove(direction)?.let { updateIOInfo(direction) }

                // Disconnect pipe
                if (sideMode == null) {
                    sideEntity.remove(direction)?.let(world::getEntity)?.discard()
                } else if (sideEntity[direction] == null) { // TODO move check and respawn IO entity to tick
                    spawnSideEntity(direction, sideMode)
                }
            }
        }
    }

    private fun updateIOInfo(from: Direction) {
        val newIO = connectedPipe[from]?.getConnectedIO(from.opposite)?.filterNot { (io, _) -> io.removed || pipeIO.containsValue(io) }
        val oldIO = connectedIO.filterValues { it.contains(from) }

        if (newIO?.equals(oldIO.mapValues { it.value.values.minOrNull()?.dec() }) == true) return

        val removedIO = oldIO.filter { newIO?.contains(it.key) != true }

        var updated = false
        removedIO.filterValues { it.minByOrNull { (_, value) -> value }?.key == from || it.remove(from).run { isEmpty } }.keys
            .let { if (connectedIO.keys.removeAll(it)) updated = true }

        newIO?.forEach { (io, distance) ->
            connectedIO.getOrPut(io) {
                updated = true
                EnumMap(net.minecraft.util.math.Direction::class.java)
            }.let {
                if (!updated && it.values.minOrNull() ?: Int.MAX_VALUE > distance + 1) updated = true
                it[from] = distance + 1
            }
        }

        // Update connected pipes
        if (updated) {
            connectedPipe.keys.forEach {
                if (it != from) world!!.blockTickScheduler.schedule(pos.offset(it), Blocks.BARRIER, 1)
            }
        }
    }

    private fun updateIOInfo() {
        val removedIO = HashMap<PipeSide, EnumSet<Direction>>()
        var updated = false

        for (side in Direction.values()) {
            val newIO = connectedPipe[side]?.getConnectedIO(side.opposite)?.filterNot { (io, _) -> io.removed || pipeIO.containsValue(io) }
            val oldIO = connectedIO.filterValues { it.contains(side) }

            if (newIO?.equals(oldIO.mapValues { it.value.values.minOrNull()?.dec() }) == true) continue

            oldIO.forEach { (io, _) -> if (newIO?.contains(io) != true) removedIO.getOrPut(io) { EnumSet.noneOf(Direction::class.java) }.add(side) }

            newIO?.forEach { (io, distance) ->
                connectedIO.getOrPut(io) {
                    updated = true
                    EnumMap(net.minecraft.util.math.Direction::class.java)
                }.let {
                    if (!updated && it.values.minOrNull() ?: Int.MAX_VALUE > distance + 1) updated = true
                    it[side] = distance + 1
                }
            }
        }

        removedIO.filter { (io, side) ->
            connectedIO[io]?.minByOrNull { it.value }?.key?.let(side::contains) == true || connectedIO[io]?.keys?.apply { removeAll(side) }?.isEmpty() == true
        }.keys.let { if (connectedIO.keys.removeAll(it)) updated = true }

        // Update connected pipes
        if (updated) {
            connectedPipe.keys.forEach { world!!.blockTickScheduler.schedule(pos.offset(it), Blocks.BARRIER, 1) }
        }
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
        this.markDirty()
    }
}
