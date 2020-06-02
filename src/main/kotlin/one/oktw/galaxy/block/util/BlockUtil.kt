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

import net.minecraft.block.*
import net.minecraft.block.Blocks.AIR
import net.minecraft.block.Blocks.BARRIER
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.TntEntity
import net.minecraft.entity.vehicle.AbstractMinecartEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import net.minecraft.block.Block as minecraftBlock

object BlockUtil {
    fun placeAndRegisterBlock(world: ServerWorld, blockPos: BlockPos, blockItem: ItemStack, blockType: BlockType): Boolean {
        val entities = world.getEntities(null, Box(blockPos))
        if (entities.any { entity -> entity is LivingEntity || entity is BoatEntity || entity is AbstractMinecartEntity || entity is TntEntity }) {
            return false
        }
        if (world.getBlockState(blockPos).block != AIR) return false
        if (world.setBlockState(blockPos, BARRIER.defaultState)) {
            CustomBlockEntityBuilder()
                .setBlockItem(blockItem)
                .setBlockType(blockType)
                .setPosition(blockPos)
                .setWorld(world)
                .setSmall()
                .create()
            playSound(world, blockPos)
            return true
        }
        return false
    }

    fun registerBlock(world: ServerWorld, blockPos: BlockPos, blockType: BlockType): Boolean {
        val entity = getCustomBlock(world, blockPos)
        if (entity != null) return false
        CustomBlockEntityBuilder()
            .setBlockType(blockType)
            .setPosition(blockPos)
            .setWorld(world)
            .setSmall()
            .create()
        return true
    }

    fun removeBlock(world: ServerWorld, blockPos: BlockPos) {
        val entity = getCustomBlock(world, blockPos) ?: return
        val block = Block(getTypeFromBlock(entity) ?: return)
        world.setBlockState(blockPos, AIR.defaultState)
        entity.kill()
        minecraftBlock.dropStack(world, blockPos, block.item!!.createItemStack())
    }

    fun unregisterBlock(world: ServerWorld, blockPos: BlockPos): Boolean {
        val entity = getCustomBlock(world, blockPos) ?: return false
        entity.kill()
        return true
    }

    fun getCustomBlock(world: ServerWorld, blockPos: BlockPos): Entity? {
        val entities = world.getEntities(null, Box(blockPos))
        return entities.firstOrNull { entity -> entity.scoreboardTags.contains("BLOCK") }
    }

    fun getTypeFromBlock(entity: Entity): BlockType? {
        val tag = entity.scoreboardTags.firstOrNull { string -> BlockType.values().map { it.name }.contains(string) }
        return if (tag != null) BlockType.valueOf(tag) else null
    }

    fun getPlacePosition(world: ServerWorld, blockPos: BlockPos, blockHitResult: BlockHitResult): BlockPos {
        return when (world.getBlockState(blockPos).block) {
            is FernBlock, is DeadBushBlock, is SeagrassBlock, is VineBlock, is TallSeagrassBlock, is TallPlantBlock, is FluidBlock -> blockPos
            is SnowBlock -> if (world.getBlockState(blockPos)[Properties.LAYERS] == 1) blockPos else blockPos.offset(blockHitResult.side)
            else -> blockPos.offset(blockHitResult.side)
        }
    }

    private fun playSound(world: ServerWorld, blockPos: BlockPos) {
        world.playSound(null, blockPos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
    }
}
