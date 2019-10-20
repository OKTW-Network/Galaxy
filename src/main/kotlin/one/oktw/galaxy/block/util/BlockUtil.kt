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

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import net.minecraft.block.Blocks.BARRIER
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.block.type.BlockType

object BlockUtil {
    suspend fun placeAndRegisterBlock(world: ServerWorld, blockItem: ItemStack, blockType: BlockType, blockPos: BlockPos): Boolean {
        val entities = world.getEntities(null, Box(blockPos))
        entities.forEach { entity ->
            if (entity.scoreboardTags.contains("BLOCK")) return false
        }
        withContext(main!!.server.asCoroutineDispatcher()) {
            world.setBlockState(blockPos, BARRIER.defaultState)
            CustomBlockEntityBuilder()
                .setBlockItem(blockItem)
                .setBlockType(blockType)
                .setPosition(blockPos)
                .setWorld(world)
                .setSmall()
                .create()
        }
        return true
    }

    suspend fun registerBlock(world: ServerWorld, blockType: BlockType, blockPos: BlockPos): Boolean {
        val entities = world.getEntities(null, Box(blockPos))
        entities.forEach { entity ->
            if (entity.scoreboardTags.contains("BLOCK")) return false
        }
        withContext(main!!.server.asCoroutineDispatcher()) {
            CustomBlockEntityBuilder()
                .setBlockType(blockType)
                .setPosition(blockPos)
                .setWorld(world)
                .setSmall()
                .create()
        }
        return true
    }

    fun removeBlock() {

    }
}
