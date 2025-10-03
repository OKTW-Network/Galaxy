/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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

import net.minecraft.item.Items.RECOVERY_COMPASS
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.Identifier

class Material private constructor(id: String, private val name: String) :
    CustomItem(Identifier.of("galaxy", "material/$id"), RECOVERY_COMPASS) {
    companion object {
        val RAW_BASE_PLATE = registry.register(Material("raw_base_plate", "item.Material.PART_RAW_BASE"))
        val BASE_PLATE = registry.register(Material("base_plate", "item.Material.PART_BASE"))
        val COOLANT = registry.register(Material("coolant", "item.Material.COOLANT"))
        val CPU = registry.register(Material("cpu", "item.Material.CPU"))
        val SCOPE = registry.register(Material("scope", "item.Material.SCOPE"))
        val BATTERY = registry.register(Material("battery", "item.Material.BATTERY"))
        val LASER = registry.register(Material("laser", "item.Material.LASER"))
        val BUTT = registry.register(Material("butt", "item.Material.BUTT"))
        val TRIGGER = registry.register(Material("trigger", "item.Material.TRIGGER"))
        val HANDLE = registry.register(Material("handle", "item.Material.HANDLE"))
        val BARREL = registry.register(Material("barrel", "item.Material.BARREL"))
    }

    override fun getName(): Text = translatable(name)
}
