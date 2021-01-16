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

package one.oktw.galaxy.item

import net.minecraft.item.ItemStack
import net.minecraft.item.Items.DIAMOND_SWORD
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import one.oktw.galaxy.item.type.GunType
import one.oktw.galaxy.item.type.ItemType.GUN
import one.oktw.galaxy.item.util.CustomItemBuilder
import one.oktw.galaxy.item.util.Gun
import java.util.*

class Gun(
    val type: GunType = GunType.DUMMY,
    val heat: Int = 20,
    val maxTemp: Int = 100,
    val cooling: Double = 1.0,
    val damage: Double = 10.0,
    val range: Double = 10.0,
    val through: Int = 0,
    val uuid: UUID = UUID.randomUUID()
) : Item {
    override val itemType = GUN

    override val baseItem: net.minecraft.item.Item = DIAMOND_SWORD

    override fun createItemStack(): ItemStack {
        val item = CustomItemBuilder()
            .setBaseItem(baseItem)
            .setModel(type.customModelData)
            .setItemType(itemType)
            .setUUID("gunUUID", uuid)
            .setCustomString("gunType", type.name)
            .setCustomInt("heat", heat)
            .setCustomInt("maxTemp", maxTemp)
            .setCustomDouble("cooling", cooling)
            .setCustomDouble("damage", damage)
            .setCustomDouble("range", range)
            .setCustomInt("through", through)
            .setUnbreakable()
            .hideAllFlags()
            .removeAllModifiers()

        if (type.languageKey != "") {
            TranslatableText(type.languageKey).styled {
                if (type in arrayOf(GunType.SNIPER, GunType.SNIPER_AIMING, GunType.RAILGUN, GunType.RAILGUN_AIMING)) {
                    it.withColor(Formatting.GREEN).withItalic(false)
                } else it.withItalic(false)
            }.let(item::setName)
        }

        Gun.fromItem(item.build())?.toLoreTag()?.let(item::setLore)

        return item.build()
    }
}
