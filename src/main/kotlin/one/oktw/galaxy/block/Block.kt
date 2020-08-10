/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.item.BlockItem
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.type.BlockType.DUMMY
import one.oktw.galaxy.block.util.CustomBlockUtil

class Block(val type: BlockType = DUMMY) {
    val item = if (type.customModelData != 0 || type.name == "DUMMY") BlockItem(type) else null

    fun register(world: ServerWorld, blockPos: BlockPos) = CustomBlockUtil.registerBlock(world, blockPos, type)

    fun place(context: ItemPlacementContext) =
        CustomBlockUtil.placeAndRegisterBlock(context, this.item!!.createItemStack(), type)
}
