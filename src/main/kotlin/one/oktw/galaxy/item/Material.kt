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

import net.minecraft.item.Items.STONE_SWORD
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

class Material private constructor(id: String, modelData: Int, private val name: String) :
    CustomItem(Identifier("galaxy", "item/material/$id"), STONE_SWORD, modelData) {
    companion object {
        val RAW_BASE_PLATE = registry.register(Material("raw_base_plate", 1010100, "item.Material.PART_RAW_BASE"))
        val BASE_PLATE = registry.register(Material("base_plate", 1010101, "item.Material.PART_BASE"))
        val COOLANT = registry.register(Material("coolant", 1010102, "item.Material.COOLANT"))
        val CPU = registry.register(Material("cpu", 1010103, "item.Material.CPU"))
        val SCOPE = registry.register(Material("scope", 1010200, "item.Material.SCOPE"))
        val BATTERY = registry.register(Material("battery", 1010300, "item.Material.BATTERY"))
        val LASER = registry.register(Material("laser", 1010400, "item.Material.LASER"))
        val BUTT = registry.register(Material("butt", 1010500, "item.Material.BUTT"))
        val TRIGGER = registry.register(Material("trigger", 1010501, "item.Material.TRIGGER"))
        val HANDLE = registry.register(Material("handle", 1010502, "item.Material.HANDLE"))
        val BARREL = registry.register(Material("barrel", 1010503, "item.Material.BARREL"))
    }

    override fun getName(): Text? = TranslatableText(name).styled { it.withItalic(false) }
}
