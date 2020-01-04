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
import net.minecraft.item.Items.DIAMOND_SWORD
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import one.oktw.galaxy.item.type.ItemType.WEAPON
import one.oktw.galaxy.item.type.WeaponType
import one.oktw.galaxy.item.type.WeaponType.DUMMY
import one.oktw.galaxy.item.util.CustomItemBuilder

class Weapon(val type: WeaponType = DUMMY) : Item {
    override val itemType = WEAPON

    override fun createItemStack(): ItemStack {
        val item = CustomItemBuilder()
            .setBaseItem(DIAMOND_SWORD)
            .setModel(type.customModelData)
            .setItemType(itemType)
            .setUnbreakable()
            .hideAllFlags()
            .removeAllModifiers()

        if (type.languageKey != "") {
            TranslatableText(type.languageKey).styled { style ->
                style.color = Formatting.GREEN //TODO Advanced weapon
                style.isItalic = false
            }.let(item::setName)
        }

        return item.build()
    }
}
