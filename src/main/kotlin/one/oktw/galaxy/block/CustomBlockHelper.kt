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

package one.oktw.galaxy.block

import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItemHelper

object CustomBlockHelper {
    private val armorStandTag = CompoundTag().apply {
        putString("id", "minecraft:armor_stand")
        putBoolean("Invisible", true)
        putBoolean("Invulnerable", true)
        putBoolean("Marker", true)
        putBoolean("NoGravity", true)
        putBoolean("Silent", true)
        putBoolean("Small", true)
        putInt("DisabledSlots", 4144959)
    }

    fun place(world: ServerWorld, pos: BlockPos, block: CustomBlock): Boolean {
        if (world.setBlockState(pos, block.baseBlock)) {
            postPlace(world, pos, block)
            return true
        }

        return false
    }

    fun place(context: ItemPlacementContext): Boolean {
        val item = CustomItemHelper.getItem(context.stack) as? CustomBlockItem ?: return false
        if ((Items.BARRIER as BlockItem).place(context).isAccepted) {
            postPlace(context.world as ServerWorld, context.blockPos, item.getBlock())
            return true
        }
        return false
    }

    fun destroy(world: ServerWorld, pos: BlockPos, drop: Boolean = true) {
        val entity = getCustomBlockEntity(world, pos)
        val blockEntity = world.getBlockEntity(pos) as? CustomBlockEntity ?: return
        world.setBlockState(pos, Blocks.AIR.defaultState)
        entity?.kill()
        if (drop) net.minecraft.block.Block.dropStack(world, pos, CustomBlock.registry.get(blockEntity.getId())!!.toItem()!!.createItemStack())
    }

    // TODO move to BlockEntity
    fun getCustomBlockEntity(world: ServerWorld, blockPos: BlockPos): Entity? {
        val entities = world.getEntitiesByType(EntityType.ARMOR_STAND) { it.blockPos == blockPos && it.scoreboardTags.contains("BLOCK") }
        return entities.firstOrNull()
    }

    /**
     * Set BlockEntity and ARMOR_STAND and play sound
     */
    private fun postPlace(world: ServerWorld, pos: BlockPos, block: CustomBlock) {
        world.setBlockEntity(pos, block.createBlockEntity())

        if (block.modelItem != null) {
            val entity: ArmorStandEntity = EntityType.getEntityFromTag(armorStandTag, world).get() as ArmorStandEntity
            entity.refreshPositionAndAngles(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.0F, 0.0F)
            entity.equipStack(EquipmentSlot.HEAD, block.modelItem)
            entity.addScoreboardTag("BLOCK")
            entity.addScoreboardTag(block.identifier.toString()) // TODO fallback
            world.spawnEntity(entity)
        }

        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
    }
}
