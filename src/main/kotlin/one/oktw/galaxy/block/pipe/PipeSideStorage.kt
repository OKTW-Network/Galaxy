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

package one.oktw.galaxy.block.pipe

import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import one.oktw.galaxy.block.entity.PipeBlockEntity
import java.util.*

class PipeSideStorage(pipe: PipeBlockEntity, side: Direction, id: UUID = UUID.randomUUID()) : PipeSideExport(pipe, side, id) {
    override val mode = PipeSideMode.STORAGE

    fun requestItem(itemStack: ItemStack) {
        // TODO
    }
}
