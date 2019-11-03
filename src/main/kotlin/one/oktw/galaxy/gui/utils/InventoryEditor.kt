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
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class InventoryEditor(type: ContainerType<out Container>, private val inventory: Inventory) {
    private val inventoryUtils = InventoryUtils(type)

    fun set(x: Int, y: Int, item: ItemStack) = set(inventoryUtils.xyToIndex(x, y), item)

    fun set(index: Int, item: ItemStack) {
        inventory.setInvStack(index, item)
    }

    fun fill(xRange: IntRange, yRange: IntRange, item: ItemStack) {
        for (x in xRange) {
            for (y in yRange) {
                inventory.setInvStack(inventoryUtils.xyToIndex(x, y), item)
            }
        }
    }

    fun fillAll(item: ItemStack) {
        for (index in 0 until inventory.invSize) inventory.setInvStack(index, item)
    }
}
