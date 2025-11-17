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
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import one.oktw.galaxy.block.listener.CustomBlockTickListener
import java.util.*
import kotlin.jvm.optionals.getOrNull

open class ModelCustomBlockEntity(type: BlockEntityType<*>, pos: BlockPos, private val modelItem: ItemStack, facing: Direction? = null) :
    CustomBlockEntity(type, pos),
    CustomBlockTickListener {

    private var entityUUID: UUID? = null
    private var checkCooldown = 0
    open var facing = facing
        set(direction) {
            if (facing != null && direction != null && direction in allowedFacing) {
                field = direction
                (level as? ServerLevel)?.getEntity(entityUUID!!)?.yRot = direction.toYRot()
                setChanged()
            }
        }
    open val allowedFacing: List<Direction> = emptyList()

    override fun tick() {
        if (entityUUID == null || --checkCooldown <= 0 && (level as ServerLevel).getEntity(entityUUID!!) == null) {
            // Kill leak entities
            (level as ServerLevel).getEntities(EntityType.ITEM_DISPLAY) { it.blockPosition() == worldPosition && it.tags.contains("BLOCK") }.forEach {
                it.kill(level as ServerLevel)
            }

            spawnEntity()
            checkCooldown = 20
        }
        if (checkCooldown <= 0) checkCooldown = 10
    }

    override fun loadAdditional(view: ValueInput) {
        super.loadAdditional(view)
        view.childOrEmpty("galaxy_data")?.getIntArray("model_entity")?.getOrNull()?.let { entityUUID = UUIDUtil.uuidFromIntArray(it) }
    }

    override fun readCopyableData(view: ValueInput) {
        super.readCopyableData(view)
        view.childOrEmpty("galaxy_data")?.getString("facing")?.getOrNull()?.let { facing = Direction.byName(it) }
    }

    override fun saveAdditional(view: ValueOutput) {
        super.saveAdditional(view)
        val data = view.child("galaxy_data")
        entityUUID?.let { data.putIntArray("model_entity", UUIDUtil.uuidToIntArray(it)) }
        facing?.let { data.putString("facing", it.name) }
    }

    override fun removeComponentsFromTag(view: ValueOutput) {
        val nbt = (view as TagValueOutput).buildResult().get("galaxy_data") as? CompoundTag ?: return
        nbt.remove("model_entity")
        nbt.remove("facing")
        if (nbt.isEmpty) view.discard("galaxy_data")
    }

    override fun setRemoved() {
        super.setRemoved()
        (level as ServerLevel).getEntity(entityUUID!!)?.kill(level as ServerLevel)
    }

    private fun spawnEntity() {
        val entity = Display.ItemDisplay(EntityType.ITEM_DISPLAY, level!!)
        entity.itemStack = modelItem
        entity.snapTo(worldPosition.x + 0.5, worldPosition.y + 0.5, worldPosition.z + 0.5, facing?.toYRot() ?: 0.0F, 0.0F)
        entity.addTag("BLOCK")
        entity.addTag(getId().toString())
        if (level!!.addFreshEntity(entity)) entityUUID = entity.uuid
        setChanged()
    }
}
