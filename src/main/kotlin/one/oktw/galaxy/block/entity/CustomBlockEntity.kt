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

package one.oktw.galaxy.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

// BlockEntity need extend
open class CustomBlockEntity(type: BlockEntityType<*>, pos: BlockPos) : BlockEntity(type, pos, Blocks.BARRIER.defaultBlockState()) {
    fun getId() = BlockEntityType.getKey(type)!!

    override fun loadAdditional(view: ValueInput) {
        super.loadAdditional(view)
        readCopyableData(view)

    }

    override fun saveAdditional(view: ValueOutput) {
        super.saveAdditional(view)
        view.putString("id", getId().toString()) // We need ID to mapping block entity, always write it.
    }

    /**
     * Read custom data from NBT, it will use on block clone.
     *
     * Also call by [loadAdditional].
     */
    open fun readCopyableData(view: ValueInput) = Unit
}
