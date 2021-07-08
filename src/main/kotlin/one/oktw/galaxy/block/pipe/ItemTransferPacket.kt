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
import java.util.*

data class ItemTransferPacket(
    val source: UUID,
    val item: ItemStack,
    val destination: UUID? = null,
    var progress: Int = 0
) {
    companion object {
        fun createFromNbt(nbt: NbtCompound): ItemTransferPacket {
            val uuid = nbt.getUuid("source")
            val item = (nbt.get("item") as NbtCompound).let(ItemStack::fromNbt)
            val destination = if (nbt.containsUuid("destination")) nbt.getUuid("destination") else null
            val progress = nbt.getInt("progress")

            return ItemTransferPacket(uuid, item, destination).also { it.progress = progress }
        }
    }

    fun toNbt(nbt: NbtCompound): NbtCompound {
        nbt.putUuid("source", source)
        nbt.put("item", item.writeNbt(NbtCompound()))
        nbt.putInt("progress", progress)
        destination?.let { nbt.putUuid("destination", it) }
        return nbt
    }
}
