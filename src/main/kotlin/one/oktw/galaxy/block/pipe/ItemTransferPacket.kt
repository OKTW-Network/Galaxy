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
    val mode: TransferMode
) {
    companion object {
        fun createFromTag(tag: NbtCompound): ItemTransferPacket {
            val uuid = tag.getUuid("source")
            val item = (tag.get("item") as NbtCompound).let(ItemStack::fromNbt)
            val mode = tag.getString("mode").let(TransferMode::valueOf)
            return ItemTransferPacket(uuid, item, mode)
        }
    }

    fun toTag(tag: NbtCompound): NbtCompound {
        tag.putUuid("source", source)
        tag.put("item", item.writeNbt(NbtCompound()))
        tag.putString("mode", mode.name)
        return tag
    }
}
