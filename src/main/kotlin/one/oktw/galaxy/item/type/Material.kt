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

package one.oktw.galaxy.item.type

import net.minecraft.item.ItemStack
import net.minecraft.item.Items.STONE_SWORD
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import one.oktw.galaxy.item.enums.ItemType.MATERIAL
import one.oktw.galaxy.item.enums.MaterialType
import one.oktw.galaxy.item.enums.MaterialType.DUMMY

class Material(val type: MaterialType = DUMMY) : Item {
    override val itemType = MATERIAL

    override fun createItemStack(): ItemStack {
        val item = ItemStack(STONE_SWORD, 1)
        val tag = CompoundTag()
        tag.putInt("CustomModelData", type.customModelData)
        tag.putBoolean("Unbreakable", true)
        // remove all modifiers(attack damage, attack speed)
        tag.put("AttributeModifiers", ListTag())
        // hide all flag
        tag.putInt("HideFlags", 63)
        item.tag = tag
        when (type) {
            //TODO Material Name
            else -> Unit
        }
        return item
    }
}
