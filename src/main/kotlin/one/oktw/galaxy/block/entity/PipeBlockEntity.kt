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

import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.block.listener.CustomBlockNeighborUpdateListener
import one.oktw.galaxy.block.pipe.ItemTransferPacket
import one.oktw.galaxy.block.pipe.SideMode
import one.oktw.galaxy.item.PipeModelItem
import one.oktw.galaxy.util.getOrCreateSubTag
import java.util.*
import kotlin.collections.HashMap

open class PipeBlockEntity(type: BlockEntityType<*>, modelItem: ItemStack) : ModelCustomBlockEntity(type, modelItem), CustomBlockClickListener,
    CustomBlockNeighborUpdateListener {
    companion object {
        private val POSITIVE_DIRECTION = Direction.values().filter { it.direction == Direction.AxisDirection.POSITIVE }
    }

    private val sideMode = HashMap<Direction, SideMode>()
    private val sideEntity = HashMap<Direction, UUID>()
    private val queue = LinkedList<ItemTransferPacket>()
    private var showingItemEntity: ItemEntity? = null
    private var needUpdatePipeConnect = true

    /**
     * Push [ItemTransferPacket] into pipe queue.
     *
     * @param item [ItemTransferPacket]
     */
    fun pushItem(item: ItemTransferPacket) {
        queue.offer(item)
    }

    override fun tick() {
        super.tick()
        if (queue.isEmpty() && showingItemEntity !== null) {
            showingItemEntity?.remove()
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
        sideEntity.values.forEach { world.getEntity(it)?.remove() }
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        val pipeData = tag.getCompound("GalaxyData").getCompound("PipeData")

        sideEntity.clear()
        sideMode.clear()
        queue.clear()
        pipeData.getCompound("SideEntity").run { keys.forEach { sideEntity[Direction.valueOf(it)] = getUuid(it) } }
        pipeData.getCompound("SideMode").run { keys.forEach { sideMode[Direction.valueOf(it)] = SideMode.valueOf(getString(it)) } }
        pipeData.getList("queue", NbtType.COMPOUND).mapTo(queue) { ItemTransferPacket.createFromTag(it as CompoundTag) }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.getOrCreateSubTag("GalaxyData").getOrCreateSubTag("PipeData").apply {
            put("Queue", queue.mapTo(ListTag()) { it.toTag(CompoundTag()) })
            put("SideMode", CompoundTag().apply { sideMode.forEach { (k, v) -> if (v != SideMode.NONE) putString(k.name, v.name) } })
            put("SideEntity", CompoundTag().apply { sideEntity.forEach { (k, v) -> putUuid(k.name, v) } })
        }

        return tag
    }

    override fun onClick(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO plug IO module or open GUI
        return ActionResult.PASS
    }

    override fun onNeighborUpdate(direct: Boolean) {
        if (direct) {
            updatePipeConnect()
            needUpdatePipeConnect = true
        } else {
            // TODO update redstone
        }
    }

    private fun setSideMode(side: Direction, mode: SideMode) {
        if (mode == SideMode.NONE) {
            sideMode.remove(side) // TODO spawn entity
        } else {
            sideMode[side] = mode
        }
    }

    private fun updatePipeConnect() {
        val world = (world as ServerWorld)
        for (direction in POSITIVE_DIRECTION) {
            if (sideMode.getOrDefault(direction, SideMode.NONE) != SideMode.NONE || world.getBlockEntity(pos.offset(direction.opposite)) !is PipeBlockEntity) {
                sideEntity.remove(direction)?.let(world::getEntity)?.remove()
                continue
            }
            if (!sideEntity.contains(direction)) {
                spawnSideEntity(direction, SideMode.NONE)
            }
        }
    }

    private fun spawnSideEntity(side: Direction, mode: SideMode) {
        val world = world as ServerWorld
        sideEntity[side]?.let(world::getEntity)?.remove()

        val entity = ItemFrameEntity(world, pos, side).apply {
            readCustomDataFromTag(CompoundTag().also(this::writeCustomDataToTag).apply { putBoolean("Fixed", true) })
            isInvisible = true
            isInvulnerable = true
            isSilent = true
            addScoreboardTag("PIPE")
            heldItemStack = when (mode) {
                SideMode.NONE -> PipeModelItem.PIPE_EXTENDED.createItemStack()
                SideMode.IMPORT -> PipeModelItem.PIPE_PORT_IMPORT.createItemStack()
                SideMode.EXPORT -> PipeModelItem.PIPE_PORT_EXPORT.createItemStack()
                SideMode.STORAGE -> PipeModelItem.PIPE_PORT_STORAGE.createItemStack()
            }
        }
        if (world.spawnEntity(entity)) sideEntity[side] = entity.uuid
    }
}
