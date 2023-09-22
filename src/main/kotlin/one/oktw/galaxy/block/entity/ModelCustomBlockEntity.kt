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

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.listener.CustomBlockTickListener
import java.util.*

open class ModelCustomBlockEntity(type: BlockEntityType<*>, pos: BlockPos, private val modelItem: ItemStack, facing: Direction? = null) :
    CustomBlockEntity(type, pos),
    CustomBlockTickListener {
    companion object {
        private val armorStandNbt = NbtCompound().apply {
            putString("id", "minecraft:armor_stand")
            putBoolean("Invisible", true)
            putBoolean("Invulnerable", true)
            putBoolean("Marker", true)
            putBoolean("NoGravity", true)
            putBoolean("Silent", true)
            putBoolean("Small", true)
            putInt("DisabledSlots", 4144959)
        }
    }

    private var entityUUID: UUID? = null
    open var facing = facing
        set(direction) {
            if (facing != null && direction != null && direction in allowedFacing) {
                field = direction
                (world as? ServerWorld)?.getEntity(entityUUID)?.yaw = direction.asRotation()
            }
        }
    open val allowedFacing: List<Direction> = emptyList()

    override fun tick() {
        if (entityUUID == null || (world as ServerWorld).getEntity(entityUUID) == null) {
            // Kill leak entities
            (world as ServerWorld).getEntitiesByType(EntityType.ARMOR_STAND) { it.blockPos == pos && it.commandTags.contains("BLOCK") }.forEach(Entity::kill)

            spawnEntity()
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        val data = nbt.get("GalaxyData") as? NbtCompound ?: return
        data.getUuid("ModelEntity")?.let { entityUUID = it }
        data.getString("Facing")?.let { facing = Direction.byName(it) }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        val data = NbtCompound()
        entityUUID?.let { data.putUuid("ModelEntity", it) }
        facing?.let { data.putString("Facing", it.getName()) }
        if (!data.isEmpty) {
            nbt.put("GalaxyData", data)
        }
    }

    override fun markRemoved() {
        super.markRemoved()
        (world as ServerWorld).getEntity(entityUUID)?.kill()
    }

    private fun spawnEntity() {
        val entity: ArmorStandEntity = EntityType.getEntityFromNbt(armorStandNbt, world).get() as ArmorStandEntity
        entity.refreshPositionAndAngles(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, facing?.asRotation() ?: 0.0F, 0.0F)
        entity.equipStack(EquipmentSlot.HEAD, modelItem)
        entity.addCommandTag("BLOCK")
        entity.addCommandTag(getId().toString())
        if (world!!.spawnEntity(entity)) entityUUID = entity.uuid
    }
}
