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

enum class UpgradeType(val customModelData: Int, val languageKey: String) {
    DUMMY(0, ""),
    BASE(2010100, ""),
    COOLING_LV1(2020100, ""),
    COOLING_LV2(2020101, ""),
    COOLING_LV3(2020102, ""),
    COOLING_LV4(2020103, ""),
    COOLING_LV5(2020104, ""),
    RANGE_LV1(2020200, ""),
    RANGE_LV2(2020201, ""),
    RANGE_LV3(2020202, ""),
    RANGE_LV4(2020203, ""),
    RANGE_LV5(2020204, ""),
    SPEED_LV1(2020300, ""),
    SPEED_LV2(2020301, ""),
    SPEED_LV3(2020302, ""),
    SPEED_LV4(2020303, ""),
    SPEED_LV5(2020304, ""),
    THROUGH_LV1(2020400, ""),
    THROUGH_LV2(2020401, ""),
    THROUGH_LV3(2020402, ""),
    THROUGH_LV4(2020403, ""),
    THROUGH_LV5(2020404, ""),
    DAMAGE_LV1(2020500, ""),
    DAMAGE_LV2(2020501, ""),
    DAMAGE_LV3(2020502, ""),
    DAMAGE_LV4(2020503, ""),
    DAMAGE_LV5(2020504, ""),
    DISTANCE_LV1(2020600, ""),
    DISTANCE_LV2(2020601, ""),
    DISTANCE_LV3(2020602, ""),
    DISTANCE_LV4(2020603, ""),
    DISTANCE_LV5(2020604, ""),
    FIRE_RESISTANCE_LV1(2020700, ""),
    FIRE_RESISTANCE_LV2(2020701, ""),
    FIRE_RESISTANCE_LV3(2020702, ""),
    FIRE_RESISTANCE_LV4(2020703, ""),
    FIRE_RESISTANCE_LV5(2020704, "")
}
