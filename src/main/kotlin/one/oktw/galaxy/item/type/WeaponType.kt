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

enum class WeaponType(val customModelData: Int) {
    DUMMY(0),
    PISTOL(1010100),
    PISTOL_LASOR(1010200),
    PISTOL_LASOR_RAINBOW(1010201),
    PISTOL_LASOR_GRAY(1010202),
    SNIPER(1010300),
    SNIPER_AIMING(1010301),
    RAILGUN(1010400),
    RAILGUN_AIMING(1010401),
    SWORD_KATANA_OFF(1020100),
    SWORD_KATANA_ON(1020101),
    SWORD_KATANA_SCABBARD(1020102),
    SWORD_MAGI_OFF(1020200),
    SWORD_MAGI_ON(1020201),
    SWORD_GHOST_CUTTER_OFF(1020300),
    SWORD_GHOST_CUTTER_ON(1020301),
    SWORD_NAZO_OFF(1020400),
    SWORD_NAZO_ON(1020401),
    SWORD_NAZO_SCABBARD(1020402),
    SWORD_RANBO_OFF(1020500),
    SWORD_RANBO_ON(1020501),
    SWORD_PLASUM_OFF(1020600),
    SWORD_PLASUM_ON(1020601),
    SWORD_NANOSABER_OFF(1020700),
    SWORD_NANOSABER_ON(1020701),
    SWORD_NANOSABER_SCABBARD(1020702)
}
