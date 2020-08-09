/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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
import net.minecraft.text.LiteralText
import one.oktw.galaxy.item.type.GuiType
import one.oktw.galaxy.item.type.GuiType.BLANK
import one.oktw.galaxy.item.type.ItemType.GUI
import one.oktw.galaxy.item.util.CustomItemBuilder

class Gui(val type: GuiType = BLANK) : Item {
    override val itemType = GUI

    override val baseItem: net.minecraft.item.Item = DIAMOND_HOE

    override fun createItemStack(): ItemStack {
        val item = CustomItemBuilder()
            .setBaseItem(baseItem)
            .setModel(type.customModelData)
            .setItemType(itemType)
            .setUnbreakable()
            .hideAllFlags()
            .removeAllModifiers()

        LiteralText("").styled { it.withItalic(false) }.let(item::setName)

        return item.build()
    }
}
