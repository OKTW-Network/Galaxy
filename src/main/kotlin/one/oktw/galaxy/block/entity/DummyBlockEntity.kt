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

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.CustomBlock

class DummyBlockEntity(type: BlockEntityType<*>, pos: BlockPos) : CustomBlockEntity(type, pos) {
    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        super.readNbt(nbt, registryLookup)
        nbt.getString("id", "")?.let(Identifier::tryParse)?.let(CustomBlock.registry::get)?.let {
            if (it != CustomBlock.DUMMY) {
                world?.removeBlockEntity(pos)
                world?.addBlockEntity(it.createBlockEntity(pos).apply { readCopyableData(nbt, registryLookup) })
            }
        }
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        // I'm dummy.
    }
}
