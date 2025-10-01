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

import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

class InventoryUtils(private val type: ScreenHandlerType<out ScreenHandler>) {
    companion object {
        val genericScreenHandlerType = listOf(
            ScreenHandlerType.GENERIC_9X1,
            ScreenHandlerType.GENERIC_9X2,
            ScreenHandlerType.GENERIC_9X3,
            ScreenHandlerType.GENERIC_9X4,
            ScreenHandlerType.GENERIC_9X5,
            ScreenHandlerType.GENERIC_9X6
        )
    }

    fun xyToIndex(x: Int, y: Int): Int {
        return when (type) {
            in genericScreenHandlerType -> y * 9 + x
            ScreenHandlerType.GENERIC_3X3 -> y * 3 + x
            ScreenHandlerType.HOPPER, ScreenHandlerType.ANVIL -> x
            else -> throw IllegalArgumentException("Unsupported container type: $type")
        }
    }

    fun indexToXY(index: Int): Pair<Int, Int> {
        return when (type) {
            in genericScreenHandlerType -> Pair(index % 9, index / 9)
            ScreenHandlerType.GENERIC_3X3 -> Pair(index % 3, index / 3)
            ScreenHandlerType.HOPPER, ScreenHandlerType.ANVIL -> Pair(index, 0)
            else -> throw IllegalArgumentException("Unsupported container type: $type")
        }
    }
}
