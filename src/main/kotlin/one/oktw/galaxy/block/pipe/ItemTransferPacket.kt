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
import net.minecraft.nbt.CompoundTag
import java.util.*

data class ItemTransferPacket(
    val source: UUID,
    val item: ItemStack,
    val mode: TransferMode
) {
    companion object {
        fun createFromTag(tag: CompoundTag): ItemTransferPacket {
            val uuid = tag.getUuid("source")
            val item = (tag.get("item") as CompoundTag).let(ItemStack::fromTag)
            val mode = tag.getString("mode").let(TransferMode::valueOf)
            return ItemTransferPacket(uuid, item, mode)
        }
    }

    fun toTag(tag: CompoundTag): CompoundTag {
        tag.putUuid("source", source)
        tag.put("item", item.toTag(CompoundTag()))
        tag.putString("mode", mode.name)
        return tag
    }
}
