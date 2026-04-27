/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2026
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

import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Blocks.BARRIER
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.block.entity.ModelCustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem

open class ModelCustomBlock(identifier: Identifier, protected open val modelItem: CustomBlockItem) : CustomBlock(identifier, BARRIER) {
    constructor(id: String, modelItem: CustomBlockItem) : this(Identifier.fromNamespaceAndPath("galaxy", "block/$id"), modelItem)

    override fun toItem() = modelItem

    override fun createBlockEntity(pos: BlockPos): CustomBlockEntity {
        return ModelCustomBlockEntity(blockEntityType, pos, modelItem)
    }
}
