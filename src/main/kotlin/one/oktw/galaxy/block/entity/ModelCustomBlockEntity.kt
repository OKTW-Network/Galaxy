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

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.listener.CustomBlockTickListener
import java.util.*

open class ModelCustomBlockEntity(type: BlockEntityType<*>, pos: BlockPos, private val modelItem: ItemStack) : CustomBlockEntity(type, pos),
    CustomBlockTickListener {
    companion object {
        private val armorStandTag = NbtCompound().apply {
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

    override fun tick() {
        if (entityUUID == null || (world as ServerWorld).getEntity(entityUUID) == null) {
            // Kill leak entities
            (world as ServerWorld).getEntitiesByType(EntityType.ARMOR_STAND) { it.blockPos == pos && it.scoreboardTags.contains("BLOCK") }.forEach(Entity::kill)

            spawnEntity()
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        entityUUID = (nbt.get("GalaxyData") as? NbtCompound)?.getUuid("ModelEntity") ?: return
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)
        entityUUID?.let { nbt.put("GalaxyData", NbtCompound().apply { putUuid("ModelEntity", it) }) }
        return nbt
    }

    override fun markRemoved() {
        super.markRemoved()
        (world as ServerWorld).getEntity(entityUUID)?.kill()
    }

    private fun spawnEntity() {
        val entity: ArmorStandEntity = EntityType.getEntityFromNbt(armorStandTag, world).get() as ArmorStandEntity
        entity.refreshPositionAndAngles(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.0F, 0.0F)
        entity.equipStack(EquipmentSlot.HEAD, modelItem)
        entity.addScoreboardTag("BLOCK")
        entity.addScoreboardTag(getId().toString())
        if (world!!.spawnEntity(entity)) entityUUID = entity.uuid
    }
}
