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

enum class SwordType(val customModelData: Int, val languageKey: String) {
    DUMMY(0, ""),
    SWORD_KATANA_OFF(1020100, "item.Weapon.KATANA"),
    SWORD_KATANA_ON(1020101, "item.Weapon.KATANA"),
    SWORD_KATANA_SCABBARD(1020102, "item.Weapon.KATANA.SCABARD"),
    SWORD_MAGI_OFF(1020200, "item.Weapon.MAGI"),
    SWORD_MAGI_ON(1020201, "item.Weapon.MAGI"),
    SWORD_GHOST_CUTTER_OFF(1020300, "item.event.halloween2018.undeadKiller"),
    SWORD_GHOST_CUTTER_ON(1020301, "item.event.halloween2018.undeadKiller"),
    SWORD_NAZO_OFF(1020400, "item.Weapon.NAZO"),
    SWORD_NAZO_ON(1020401, "item.Weapon.NAZO"),
    SWORD_NAZO_SCABBARD(1020402, "item.Weapon.NAZO.SCABARD"),
    SWORD_RANBO_OFF(1020500, "item.Weapon.RANBO"),
    SWORD_RANBO_ON(1020501, "item.Weapon.RANBO"),
    SWORD_PLASUM_OFF(1020600, "item.Weapon.PLASUM"),
    SWORD_PLASUM_ON(1020601, "item.Weapon.PLASUM"),
    SWORD_NANOSABER_OFF(1020700, "item.Weapon.NANOSABER"),
    SWORD_NANOSABER_ON(1020701, "item.Weapon.NANOSABER"),
    SWORD_NANOSABER_SCABBARD(1020702, "item.Weapon.NANOSABER.SCABARD")
}
