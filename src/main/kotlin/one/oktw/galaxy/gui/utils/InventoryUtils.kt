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

package one.oktw.galaxy.gui.utils

import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

class InventoryUtils(private val type: MenuType<out AbstractContainerMenu>) {
    companion object {
        val genericScreenHandlerType = listOf(
            MenuType.GENERIC_9x1,
            MenuType.GENERIC_9x2,
            MenuType.GENERIC_9x3,
            MenuType.GENERIC_9x4,
            MenuType.GENERIC_9x5,
            MenuType.GENERIC_9x6
        )
    }

    fun xyToIndex(x: Int, y: Int): Int {
        return when (type) {
            in genericScreenHandlerType -> y * 9 + x
            MenuType.GENERIC_3x3 -> y * 3 + x
            MenuType.HOPPER, MenuType.ANVIL -> x
            else -> throw IllegalArgumentException("Unsupported container type: $type")
        }
    }

    fun indexToXY(index: Int): Pair<Int, Int> {
        return when (type) {
            in genericScreenHandlerType -> Pair(index % 9, index / 9)
            MenuType.GENERIC_3x3 -> Pair(index % 3, index / 3)
            MenuType.HOPPER, MenuType.ANVIL -> Pair(index, 0)
            else -> throw IllegalArgumentException("Unsupported container type: $type")
        }
    }
}
