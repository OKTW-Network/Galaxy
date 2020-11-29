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

package one.oktw.galaxy.item.type

enum class GunType(val customModelData: Int, val languageKey: String) {
    DUMMY(0, ""),
    PISTOL(1010100, "item.Gun.PISTOL"),
    PISTOL_LASOR(1010200, "item.Gun.PISTOL"),
    PISTOL_LASOR_AIMING(1010201, "item.Gun.PISTOL"),
    SNIPER(1010300, "item.Gun.SNIPER"),
    SNIPER_AIMING(1010301, "item.Gun.SNIPER"),
    RAILGUN(1010400, "item.Gun.RAILGUN"),
    RAILGUN_AIMING(1010401, "item.Gun.RAILGUN")
}
