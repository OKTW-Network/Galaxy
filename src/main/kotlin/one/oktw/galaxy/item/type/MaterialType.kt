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

enum class MaterialType(val customModelData: Int, val languageKey: String) {
    DUMMY(0, ""),
    RAW_BASE_PLATE(1010100, "item.Material.PART_RAW_BASE"),
    BASE_PLATE(1010101, "item.Material.PART_BASE"),
    COOLANT(1010102, "item.Material.COOLANT"),
    CPU(1010103, "item.Material.CPU"),
    SCOPE(1010200, "item.Material.SCOPE"),
    BATTERY(1010300, "item.Material.BATTERY"),
    LASER(1010400, "item.Material.LASER"),
    BUTT(1010500, "item.Material.BUTT"),
    TRIGGER(1010501, "item.Material.TRIGGER"),
    HANDLE(1010502, "item.Material.HANDLE"),
    BARREL(1010503, "item.Material.BARREL")
}
