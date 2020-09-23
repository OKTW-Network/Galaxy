/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.item.util

import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.AIR
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.text.Text
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.item.type.ItemType

class CustomItemBuilder {
    private var baseItem: ItemConvertible = AIR
    private val tags: CompoundTag = CompoundTag()
    private var name: Text? = null

    fun setBaseItem(item: ItemConvertible): CustomItemBuilder {
        this.baseItem = item
        return this
    }

    fun setModel(customModelData: Int): CustomItemBuilder {
        this.tags.putInt("CustomModelData", customModelData)
        return this
    }

    fun setName(name: Text): CustomItemBuilder {
        this.name = name
        return this
    }

    fun setUnbreakable(): CustomItemBuilder {
        this.tags.putBoolean("Unbreakable", true)
        return this
    }

    fun hideAllFlags(): CustomItemBuilder {
        this.tags.putInt("HideFlags", Flags.ALL.id)
        return this
    }

    fun hideFlags(flags: List<Flags>): CustomItemBuilder {
        var id = 0
        flags.forEach {
            if (it == Flags.ALL) {
                id = it.id
                return@forEach
            }
            id += it.id
        }
        this.tags.putInt("HideFlags", id)
        return this
    }

    fun removeAllModifiers(): CustomItemBuilder {
        this.tags.put("AttributeModifiers", ListTag())
        return this
    }

    fun setItemType(itemType: ItemType): CustomItemBuilder {
        this.tags.putString("customItemType", itemType.name)
        return this
    }

    fun setBlockType(blockType: BlockType): CustomItemBuilder {
        this.tags.putString("customBlockType", blockType.name)
        return this
    }

    fun build(): ItemStack {
        val item = ItemStack(this.baseItem, 1)
        item.tag = this.tags
        if (this.name != null) {
            this.name.let(item::setCustomName)
        }
        return item
    }
}
