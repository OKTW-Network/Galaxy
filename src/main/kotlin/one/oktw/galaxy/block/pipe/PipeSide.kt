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

package one.oktw.galaxy.block.pipe

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.entity.PipeBlockEntity
import one.oktw.galaxy.block.pipe.PipeSideMode.*
import java.util.*

abstract class PipeSide(protected val pipe: PipeBlockEntity, protected val side: Direction, val id: UUID = UUID.randomUUID(), open val mode: PipeSideMode) {
    companion object {
        fun createFromNBT(pipe: PipeBlockEntity, side: Direction, nbt: NbtCompound): PipeSide? {
            val mode = try {
                valueOf(nbt.getString("mode"))
            } catch (e: IllegalArgumentException) {
                null
            }
            return when (mode) {
                STORAGE -> PipeSideStorage(pipe, side, nbt.getUuid("id"))
                IMPORT -> PipeSideImport(pipe, side, nbt.getUuid("id"))
                EXPORT -> PipeSideExport(pipe, side, nbt.getUuid("id"))
                else -> null
            }
        }
    }

    open fun writeNBT(nbt: NbtCompound): NbtCompound {
        nbt.putUuid("id", id)
        nbt.putString("mode", mode.name)

        return nbt
    }

    open fun tick() = Unit

    open fun input(): ItemStack = ItemStack.EMPTY

    open fun output(item: ItemStack): ItemStack = item

    override fun toString(): String {
        return "PipeIO(id=$id, mode=$mode)"
    }
}
