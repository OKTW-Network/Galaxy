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
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.ProblemReporter
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.TagValueInput
import one.oktw.galaxy.block.entity.ModelCustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItemHelper

object CustomBlockHelper {
    fun place(context: BlockPlaceContext): Boolean {
        val item = CustomItemHelper.getItem(context.itemInHand) as? CustomBlockItem ?: return false
        val stack2 = context.itemInHand.copy()
        if ((Items.BARRIER as BlockItem).place(context).consumesAction()) {
            val world = context.level
            val pos = context.clickedPos

            // Clear waterlogged
            world.setBlockAndUpdate(pos, world.getBlockState(pos).trySetValue(BlockStateProperties.WATERLOGGED, false))

            // Create block entity and read data
            val entity = item.getBlock().createBlockEntity(pos)
            stack2.get(DataComponents.BLOCK_ENTITY_DATA)?.let {
                ProblemReporter.ScopedCollector(entity.problemPath(), LogUtils.getLogger()).use { reporter ->
                    entity.readCopyableData(TagValueInput.create(reporter, world.registryAccess(), it.copyTagWithoutId()))
                }
            }
            entity.applyComponentsFromItemStack(stack2)
            // Set facing
            if (entity is ModelCustomBlockEntity) {
                val allowed = entity.allowedFacing
                val facing = context.player?.let(Direction::orderedByNearest) ?: arrayOf(context.clickedFace)
                entity.facing = facing.firstOrNull { it.opposite in allowed }?.opposite
            }
            world.setBlockEntity(entity)

            world.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F)
            return true
        }
        return false
    }

    fun destroyAndDrop(world: ServerLevel, pos: BlockPos) {
        val blockEntity = world.getBlockEntity(pos) as? ModelCustomBlockEntity ?: return
        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
        Block.popResource(world, pos, CustomBlock.registry.get(blockEntity.getId())!!.toItem()!!.createItemStack())

        world.playSound(null, pos, SoundEvents.METAL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F)
    }
}
