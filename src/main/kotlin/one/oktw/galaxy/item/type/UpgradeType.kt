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

enum class UpgradeType(val customModelData: Int, val languageKey: String, val level: Int) {
    DUMMY(0, "", 0),
    BASE(2010100, "item.Upgrade.BASE", 0),
    COOLING_LV1(2020100, "item.Upgrade.COOLING", 1),
    COOLING_LV2(2020101, "item.Upgrade.COOLING", 2),
    COOLING_LV3(2020102, "item.Upgrade.COOLING", 3),
    COOLING_LV4(2020103, "item.Upgrade.COOLING", 4),
    COOLING_LV5(2020104, "item.Upgrade.COOLING", 5),
    RANGE_LV1(2020200, "item.Upgrade.RANGE", 1),
    RANGE_LV2(2020201, "item.Upgrade.RANGE", 2),
    RANGE_LV3(2020202, "item.Upgrade.RANGE", 3),
    RANGE_LV4(2020203, "item.Upgrade.RANGE", 4),
    RANGE_LV5(2020204, "item.Upgrade.RANGE", 5),
    SPEED_LV1(2020300, "item.Upgrade.SPEED", 1),
    SPEED_LV2(2020301, "item.Upgrade.SPEED", 2),
    SPEED_LV3(2020302, "item.Upgrade.SPEED", 3),
    SPEED_LV4(2020303, "item.Upgrade.SPEED", 4),
    SPEED_LV5(2020304, "item.Upgrade.SPEED", 5),
    THROUGH_LV1(2020400, "item.Upgrade.THROUGH", 1),
    THROUGH_LV2(2020401, "item.Upgrade.THROUGH", 2),
    THROUGH_LV3(2020402, "item.Upgrade.THROUGH", 3),
    THROUGH_LV4(2020403, "item.Upgrade.THROUGH", 4),
    THROUGH_LV5(2020404, "item.Upgrade.THROUGH", 5),
    DAMAGE_LV1(2020500, "item.Upgrade.DAMAGE", 1),
    DAMAGE_LV2(2020501, "item.Upgrade.DAMAGE", 2),
    DAMAGE_LV3(2020502, "item.Upgrade.DAMAGE", 3),
    DAMAGE_LV4(2020503, "item.Upgrade.DAMAGE", 4),
    DAMAGE_LV5(2020504, "item.Upgrade.DAMAGE", 5),
    DISTANCE_LV1(2020600, "item.Upgrade.DISTANCE", 1),
    DISTANCE_LV2(2020601, "item.Upgrade.DISTANCE", 2),
    DISTANCE_LV3(2020602, "item.Upgrade.DISTANCE", 3),
    DISTANCE_LV4(2020603, "item.Upgrade.DISTANCE", 4),
    DISTANCE_LV5(2020604, "item.Upgrade.DISTANCE", 5),
    FIRE_RESISTANCE_LV1(2020700, "item.Upgrade.HEAT", 1),
    FIRE_RESISTANCE_LV2(2020701, "item.Upgrade.HEAT", 2),
    FIRE_RESISTANCE_LV3(2020702, "item.Upgrade.HEAT", 3),
    FIRE_RESISTANCE_LV4(2020703, "item.Upgrade.HEAT", 4),
    FIRE_RESISTANCE_LV5(2020704, "item.Upgrade.HEAT", 5)
}
