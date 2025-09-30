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
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties
import net.minecraft.storage.NbtReadView
import net.minecraft.util.ErrorReporter
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.entity.ModelCustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItemHelper

object CustomBlockHelper {
    fun place(context: ItemPlacementContext): Boolean {
        val item = CustomItemHelper.getItem(context.stack) as? CustomBlockItem ?: return false
        val stack2 = context.stack.copy()
        if ((Items.BARRIER as BlockItem).place(context).isAccepted) {
            // Clear waterlogged
            val newState = context.world.getBlockState(context.blockPos).withIfExists(Properties.WATERLOGGED, false)
            context.world.setBlockState(context.blockPos, newState)

            postPlace(context.world as ServerWorld, context.blockPos, item.getBlock(), context.placementDirections, stack2)
            return true
        }
        return false
    }

    fun destroyAndDrop(world: ServerWorld, pos: BlockPos) {
        val blockEntity = world.getBlockEntity(pos) as? ModelCustomBlockEntity ?: return
        world.setBlockState(pos, Blocks.AIR.defaultState)
        Block.dropStack(world, pos, CustomBlock.registry.get(blockEntity.getId())!!.toItem()!!.createItemStack())
    }

    /**
     * Set BlockEntity and play sound
     */
    private fun postPlace(world: ServerWorld, pos: BlockPos, block: CustomBlock, direction: Array<Direction>? = null, stack: ItemStack? = null) {
        val entity = block.createBlockEntity(pos)
        if (stack != null) {
            entity.readComponents(stack)
            val nbt = stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).copyNbt()
            val reporter = ErrorReporter.Logging(entity.reporterContext, LogUtils.getLogger())
            entity.readCopyableData(NbtReadView.create(reporter, world.registryManager, nbt))
        }
        if (entity is ModelCustomBlockEntity) {
            val allowed = entity.allowedFacing
            entity.facing = direction?.firstOrNull { it.opposite in allowed }?.opposite
        }
        world.addBlockEntity(entity)

        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
    }
}
