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

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.Uuids
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.listener.CustomBlockTickListener
import java.util.*

open class ModelCustomBlockEntity(type: BlockEntityType<*>, pos: BlockPos, private val modelItem: ItemStack, facing: Direction? = null) :
    CustomBlockEntity(type, pos),
    CustomBlockTickListener {

    private var entityUUID: UUID? = null
    open var facing = facing
        set(direction) {
            if (facing != null && direction != null && direction in allowedFacing) {
                field = direction
                (world as? ServerWorld)?.getEntity(entityUUID)?.yaw = direction.positiveHorizontalDegrees
            }
        }
    open val allowedFacing: List<Direction> = emptyList()

    override fun tick() {
        if (entityUUID == null || (world as ServerWorld).getEntity(entityUUID) == null) {
            // Kill leak entities
            (world as ServerWorld).getEntitiesByType(EntityType.ARMOR_STAND) { it.blockPos == pos && it.commandTags.contains("BLOCK") }.forEach {
                it.kill(world as ServerWorld)
            }

            spawnEntity()
        }
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        val data = view.getReadView("galaxy_data") ?: return
        data.getOptionalIntArray("model_entity")?.let { entityUUID = Uuids.toUuid(it.get()) }
        data.getString("facing", "")?.let { facing = Direction.byId(it) }
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        val data = view.get("galaxy_data")
        entityUUID?.let { data.putIntArray("model_entity", Uuids.toIntArray(it)) }
        facing?.let { data.putString("facing", it.id) }
    }

    override fun markRemoved() {
        super.markRemoved()
        (world as ServerWorld).getEntity(entityUUID)?.kill(world as ServerWorld)
    }

    private fun spawnEntity() {
        val entity = DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, world)
        entity.itemStack = modelItem
        entity.refreshPositionAndAngles(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, facing?.positiveHorizontalDegrees ?: 0.0F, 0.0F)
        entity.addCommandTag("BLOCK")
        entity.addCommandTag(getId().toString())
        if (world!!.spawnEntity(entity)) entityUUID = entity.uuid
    }
}
