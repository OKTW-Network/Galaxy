/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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

package one.oktw.galaxy.block

import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.block.entity.HarvestBlockEntity
import one.oktw.galaxy.item.CustomBlockItem

class HarvestBlock : ModelCustomBlock("harvest", CustomBlockItem.HARVEST.createItemStack()) {
    override fun createBlockEntity(pos: BlockPos): CustomBlockEntity {
        return HarvestBlockEntity(blockEntityType, pos, modelItem)
    }
}
