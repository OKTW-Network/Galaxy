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
import net.minecraft.item.Items.DIAMOND_SWORD
import net.minecraft.text.TranslatableText
import one.oktw.galaxy.item.type.ItemType.SWORD
import one.oktw.galaxy.item.type.SwordType
import one.oktw.galaxy.item.util.CustomItemBuilder

class Sword(val type: SwordType = SwordType.DUMMY) : Item {
    override val itemType = SWORD

    override val baseItem: net.minecraft.item.Item = DIAMOND_SWORD

    override fun createItemStack(): ItemStack {
        val item = CustomItemBuilder()
            .setBaseItem(baseItem)
            .setModel(type.customModelData)
            .setItemType(itemType)
            .setUnbreakable()
            .hideAllFlags()
            .removeAllModifiers()

        if (type.languageKey != "") {
            TranslatableText(type.languageKey).styled {
                it.withItalic(false) //TODO Advanced weapon
            }.let(item::setName)
        }

        return item.build()
    }
}
