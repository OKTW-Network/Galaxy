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

enum class MaterialType(val customModelData: Int, val languageKey: String) {
    DUMMY(0, ""),
    RAW_BASE_PLATE(2010100, "item.Material.PART_RAW_BASE"),
    BASE_PLATE(2010101, "item.Material.PART_BASE"),
    COOLANT(2010102, "item.Material.COOLANT"),
    CPU(2010103, "item.Material.CPU"),
    SCOPE(2010200, "item.Material.SCOPE"),
    BATTERY(2010300, "item.Material.BATTERY"),
    LASER(2010400, "item.Material.LASER"),
    BUTT(2010500, "item.Material.BUTT"),
    TRIGGER(2010501, "item.Material.TRIGGER"),
    HANDLE(2010502, "item.Material.HANDLE"),
    BARREL(2010503, "item.Material.BARREL")
}
