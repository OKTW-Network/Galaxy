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

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import kotlin.jvm.optionals.getOrNull

object CustomItemHelper {
    fun getNbt(itemStack: ItemStack): CompoundTag {
        val galaxyData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
        return galaxyData.getCompoundOrEmpty("galaxy_data")
    }

    fun getItem(itemStack: ItemStack): CustomItem? {
        val customNbt = getNbt(itemStack)

        return customNbt.read("custom_item_identifier", Identifier.CODEC).getOrNull()
            ?.let(CustomItem.registry::get)
            ?.run { readCustomNbt(customNbt) }
    }
}
