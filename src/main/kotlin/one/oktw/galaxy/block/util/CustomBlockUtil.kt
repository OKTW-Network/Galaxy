/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

import net.minecraft.block.Blocks.AIR
import net.minecraft.entity.Entity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items.BARRIER
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import net.minecraft.block.Block as minecraftBlock

object CustomBlockUtil {
    fun placeAndRegisterBlock(context: ItemUsageContext, blockItem: ItemStack, blockType: BlockType): Boolean {
        val world = context.world as ServerWorld
        val placementContext = ItemPlacementContext(context)
        val blockPos = placementContext.blockPos
        val placeResult = (BARRIER as BlockItem).place(placementContext)
        if (placeResult == ActionResult.SUCCESS) {
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
        val entity = getCustomBlockEntity(world, blockPos)
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
        val entity = getCustomBlockEntity(world, blockPos) ?: return
        val block = Block(getTypeFromCustomBlockEntity(entity) ?: return)
        world.setBlockState(blockPos, AIR.defaultState)
        entity.kill()
        minecraftBlock.dropStack(world, blockPos, block.item!!.createItemStack())
    }

    fun unregisterBlock(world: ServerWorld, blockPos: BlockPos): Boolean {
        val entity = getCustomBlockEntity(world, blockPos) ?: return false
        entity.kill()
        return true
    }

    fun getCustomBlockEntity(world: ServerWorld, blockPos: BlockPos): Entity? {
        val entities = world.getEntities(null, Box(blockPos))
        return entities.firstOrNull { entity -> entity.scoreboardTags.contains("BLOCK") }
    }

    fun getTypeFromCustomBlockEntity(entity: Entity): BlockType? {
        val tag = entity.scoreboardTags.firstOrNull { string -> BlockType.values().map { it.name }.contains(string) }
        return if (tag != null) BlockType.valueOf(tag) else null
    }

    private fun playSound(world: ServerWorld, blockPos: BlockPos) {
        world.playSound(null, blockPos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
    }
}
