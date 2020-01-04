/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.block.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.type.BlockType

class CustomBlockEntityBuilder {
    private val tags: CompoundTag = CompoundTag()
    private var blockItem: ItemStack? = null
    private var world: ServerWorld? = null
    private var blockPos: BlockPos = BlockPos(0, 0, 0)
    private var blockType: BlockType = BlockType.DUMMY

    fun setSmall(): CustomBlockEntityBuilder {
        this.tags.putBoolean("Small", true)
        return this
    }

    fun setWorld(world: ServerWorld): CustomBlockEntityBuilder {
        this.world = world
        return this
    }

    fun setPosition(blockPos: BlockPos): CustomBlockEntityBuilder {
        this.blockPos = blockPos
        return this
    }

    fun setBlockItem(item: ItemStack): CustomBlockEntityBuilder {
        this.blockItem = item
        return this
    }

    fun setBlockType(blockType: BlockType): CustomBlockEntityBuilder {
        this.blockType = blockType
        return this
    }

    fun create(): Entity {
        if (this.world == null) throw RuntimeException("World is null!")
        this.tags.putString("id", "minecraft:armor_stand")
        this.tags.putBoolean("Invisible", true)
        this.tags.putBoolean("Invulnerable", true)
        this.tags.putBoolean("NoGravity", true)
        this.tags.putBoolean("Silent", true)
        val entity = EntityType.getEntityFromTag(this.tags, this.world).get()
        entity.setPositionAndAngles(this.blockPos, 0.0F, 0.0F)
        if (this.blockItem != null) {
            entity.equip(EquipmentSlot.HEAD.armorStandSlotId, this.blockItem)
        }
        entity.addScoreboardTag("BLOCK")
        entity.addScoreboardTag(this.blockType.name)
        this.world!!.tryLoadEntity(entity)
        return entity
    }
}
