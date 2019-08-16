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

package one.oktw.galaxy.item

import net.minecraft.item.ItemStack
import net.minecraft.item.Items.DIAMOND_HOE
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.text.LiteralText
import one.oktw.galaxy.item.type.GuiType
import one.oktw.galaxy.item.type.GuiType.BLANK
import one.oktw.galaxy.item.type.ItemType.GUI

class Gui(val type: GuiType = BLANK) : Item {
    override val itemType = GUI

    override fun createItemStack(): ItemStack {
        val item = ItemStack(DIAMOND_HOE, 1)
        val tag = CompoundTag()
        tag.putInt("CustomModelData", type.customModelData)
        tag.putBoolean("Unbreakable", true)
        // remove all modifiers(attack damage, attack speed)
        tag.put("AttributeModifiers", ListTag())
        // hide all flag
        tag.putInt("HideFlags", 63)
        item.tag = tag
        item.setCustomName(LiteralText("").styled { style -> style.isItalic = false })
        return item
    }
}
