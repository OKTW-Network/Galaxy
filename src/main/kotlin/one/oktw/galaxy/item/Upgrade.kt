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

import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

class Upgrade(id: String, modelData: Int, private val name: String, private val level: Int) :
    CustomItem(Identifier("galaxy", "item/upgrade/$id"), Items.IRON_SWORD, modelData) {
    companion object {
        val BASE = registry.register(Upgrade("base", 2010100, "item.Upgrade.BASE", 0))
        val COOLING_LV1 = registry.register(Upgrade("cooling_lv1", 2020100, "item.Upgrade.COOLING", 1))
        val COOLING_LV2 = registry.register(Upgrade("cooling_lv2", 2020101, "item.Upgrade.COOLING", 2))
        val COOLING_LV3 = registry.register(Upgrade("cooling_lv3", 2020102, "item.Upgrade.COOLING", 3))
        val COOLING_LV4 = registry.register(Upgrade("cooling_lv4", 2020103, "item.Upgrade.COOLING", 4))
        val COOLING_LV5 = registry.register(Upgrade("cooling_lv5", 2020104, "item.Upgrade.COOLING", 5))
        val RANGE_LV1 = registry.register(Upgrade("range_lv1", 2020200, "item.Upgrade.RANGE", 1))
        val RANGE_LV2 = registry.register(Upgrade("range_lv2", 2020201, "item.Upgrade.RANGE", 2))
        val RANGE_LV3 = registry.register(Upgrade("range_lv3", 2020202, "item.Upgrade.RANGE", 3))
        val RANGE_LV4 = registry.register(Upgrade("range_lv4", 2020203, "item.Upgrade.RANGE", 4))
        val RANGE_LV5 = registry.register(Upgrade("range_lv5", 2020204, "item.Upgrade.RANGE", 5))
        val SPEED_LV1 = registry.register(Upgrade("speed_lv1", 2020300, "item.Upgrade.SPEED", 1))
        val SPEED_LV2 = registry.register(Upgrade("speed_lv2", 2020301, "item.Upgrade.SPEED", 2))
        val SPEED_LV3 = registry.register(Upgrade("speed_lv3", 2020302, "item.Upgrade.SPEED", 3))
        val SPEED_LV4 = registry.register(Upgrade("speed_lv4", 2020303, "item.Upgrade.SPEED", 4))
        val SPEED_LV5 = registry.register(Upgrade("speed_lv5", 2020304, "item.Upgrade.SPEED", 5))
        val THROUGH_LV1 = registry.register(Upgrade("through_lv1", 2020400, "item.Upgrade.THROUGH", 1))
        val THROUGH_LV2 = registry.register(Upgrade("through_lv2", 2020401, "item.Upgrade.THROUGH", 2))
        val THROUGH_LV3 = registry.register(Upgrade("through_lv3", 2020402, "item.Upgrade.THROUGH", 3))
        val THROUGH_LV4 = registry.register(Upgrade("through_lv4", 2020403, "item.Upgrade.THROUGH", 4))
        val THROUGH_LV5 = registry.register(Upgrade("through_lv5", 2020404, "item.Upgrade.THROUGH", 5))
        val DAMAGE_LV1 = registry.register(Upgrade("damage_lv1", 2020500, "item.Upgrade.DAMAGE", 1))
        val DAMAGE_LV2 = registry.register(Upgrade("damage_lv2", 2020501, "item.Upgrade.DAMAGE", 2))
        val DAMAGE_LV3 = registry.register(Upgrade("damage_lv3", 2020502, "item.Upgrade.DAMAGE", 3))
        val DAMAGE_LV4 = registry.register(Upgrade("damage_lv4", 2020503, "item.Upgrade.DAMAGE", 4))
        val DAMAGE_LV5 = registry.register(Upgrade("damage_lv5", 2020504, "item.Upgrade.DAMAGE", 5))
        val DISTANCE_LV1 = registry.register(Upgrade("distance_lv1", 2020600, "item.Upgrade.DISTANCE", 1))
        val DISTANCE_LV2 = registry.register(Upgrade("distance_lv2", 2020601, "item.Upgrade.DISTANCE", 2))
        val DISTANCE_LV3 = registry.register(Upgrade("distance_lv3", 2020602, "item.Upgrade.DISTANCE", 3))
        val DISTANCE_LV4 = registry.register(Upgrade("distance_lv4", 2020603, "item.Upgrade.DISTANCE", 4))
        val DISTANCE_LV5 = registry.register(Upgrade("distance_lv5", 2020604, "item.Upgrade.DISTANCE", 5))
        val FIRE_RESISTANCE_LV1 = registry.register(Upgrade("fire_resistance_lv1", 2020700, "item.Upgrade.HEAT", 1))
        val FIRE_RESISTANCE_LV2 = registry.register(Upgrade("fire_resistance_lv2", 2020701, "item.Upgrade.HEAT", 2))
        val FIRE_RESISTANCE_LV3 = registry.register(Upgrade("fire_resistance_lv3", 2020702, "item.Upgrade.HEAT", 3))
        val FIRE_RESISTANCE_LV4 = registry.register(Upgrade("fire_resistance_lv4", 2020703, "item.Upgrade.HEAT", 4))
        val FIRE_RESISTANCE_LV5 = registry.register(Upgrade("fire_resistance_lv5", 2020704, "item.Upgrade.HEAT", 5))
    }

    override fun getName(): Text? = TranslatableText("item.Upgrade.Item", TranslatableText(name))
        .also { if (level > 0) it.append(" Lv.${level}") }
        .styled { it.withItalic(false) }
}
