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
import one.oktw.galaxy.item.type.ItemType.UPGRADE
import one.oktw.galaxy.item.type.UpgradeType
import one.oktw.galaxy.item.type.UpgradeType.DUMMY
import one.oktw.galaxy.item.util.CustomItemBuilder

class Upgrade(val type: UpgradeType = DUMMY) : Item {
    override val itemType = UPGRADE

    override fun createItemStack(): ItemStack {
        val item = CustomItemBuilder()
            .setBaseItem(DIAMOND_SWORD)
            .setModel(type.customModelData)
            .setUnbreakable()
            .hideAllFlags()
            .removeAllModifiers()

        if (type.languageKey != "") {
            val name = TranslatableText("item.Upgrade.Item", TranslatableText(type.languageKey))
            if (type.level > 0) {
                name.append(" Lv.${type.level}")
            }
            name.styled { style ->
                style.isItalic = false
            }.let(item::setName)
        }
        return item.build()
    }
}
