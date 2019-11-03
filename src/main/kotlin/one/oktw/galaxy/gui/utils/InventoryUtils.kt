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

package one.oktw.galaxy.gui.utils

import net.minecraft.container.Container
import net.minecraft.container.ContainerType

class InventoryUtils(private val type: ContainerType<out Container>) {
    companion object {
        val genericContainerType = listOf(
            ContainerType.GENERIC_9X1,
            ContainerType.GENERIC_9X2,
            ContainerType.GENERIC_9X3,
            ContainerType.GENERIC_9X4,
            ContainerType.GENERIC_9X5,
            ContainerType.GENERIC_9X6
        )
    }

    fun xyToIndex(x: Int, y: Int): Int {
        return when (type) {
            in genericContainerType -> y * 9 + x
            ContainerType.GENERIC_3X3 -> y * 3 + x
            ContainerType.HOPPER -> x
            else -> throw IllegalArgumentException("Unsupported container type: $type")
        }
    }

    fun indexToXY(index: Int): Pair<Int, Int> {
        return when (type) {
            in genericContainerType -> Pair(index % 9, index / 9)
            ContainerType.GENERIC_3X3 -> Pair(index % 3, index / 3)
            ContainerType.HOPPER -> Pair(index, 0)
            else -> throw IllegalArgumentException("Unsupported container type: $type")
        }
    }
}
