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

package one.oktw.galaxy.item.enums

import java.util.Arrays.asList

enum class UpgradeType(val id: Int, val extraIds: List<Int>? = null) {
    BASE(16),

    // Machine
    RANGE(999),
    SPEED(998),

    // Weapon or Tool
    DAMAGE(997),
    COOLING(9999, asList(17, 18, 19, 20, 21)),
    HEAT(996),
    THROUGH(995),

    // Armor
    SHIELD(994),
    FLEXIBLE(993),
    ADAPT(992),
    FLY(991),
    NIGHT_VISION(990),
    GPS(989),
    DETECTOR(988)
}
