/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2024
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

import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos

// BlockEntity need extend
open class CustomBlockEntity(type: BlockEntityType<*>, pos: BlockPos) : BlockEntity(type, pos, Blocks.BARRIER.defaultState) {
    fun getId() = BlockEntityType.getId(type)!!

    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        super.readNbt(nbt, registryLookup)
        readCopyableData(nbt, registryLookup)
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        super.writeNbt(nbt, registryLookup)
        nbt.putString("id", getId().toString()) // We need ID to mapping block entity, always write it.
    }

    /**
     * Read custom data from NBT, it will use on block clone.
     *
     * Also call by [readNbt].
     */
    open fun readCopyableData(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) = Unit
}
