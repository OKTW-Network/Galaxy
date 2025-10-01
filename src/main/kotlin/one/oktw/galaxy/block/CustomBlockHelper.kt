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

package one.oktw.galaxy.block

import com.mojang.logging.LogUtils
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties
import net.minecraft.storage.NbtReadView
import net.minecraft.util.ErrorReporter
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.entity.ModelCustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItemHelper

object CustomBlockHelper {
    fun place(context: ItemPlacementContext): Boolean {
        val item = CustomItemHelper.getItem(context.stack) as? CustomBlockItem ?: return false
        val stack2 = context.stack.copy()
        if ((Items.BARRIER as BlockItem).place(context).isAccepted) {
            val world = context.world
            val pos = context.blockPos

            // Clear waterlogged
            world.setBlockState(pos, world.getBlockState(pos).withIfExists(Properties.WATERLOGGED, false))

            // Create block entity and read data
            val entity = item.getBlock().createBlockEntity(pos)
            stack2.get(DataComponentTypes.BLOCK_ENTITY_DATA)?.let {
                val reporter = ErrorReporter.Logging(entity.reporterContext, LogUtils.getLogger())
                entity.readCopyableData(NbtReadView.create(reporter, world.registryManager, it.copyNbtWithoutId()))
            }
            entity.readComponents(stack2)
            // Set facing
            if (entity is ModelCustomBlockEntity) {
                val allowed = entity.allowedFacing
                entity.facing = context.placementDirections?.firstOrNull { it.opposite in allowed }?.opposite
            }
            world.addBlockEntity(entity)

            world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
            return true
        }
        return false
    }

    fun destroyAndDrop(world: ServerWorld, pos: BlockPos) {
        val blockEntity = world.getBlockEntity(pos) as? ModelCustomBlockEntity ?: return
        world.setBlockState(pos, Blocks.AIR.defaultState)
        Block.dropStack(world, pos, CustomBlock.registry.get(blockEntity.getId())!!.toItem()!!.createItemStack())

        world.playSound(null, pos, SoundEvents.BLOCK_METAL_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F)
    }
}
