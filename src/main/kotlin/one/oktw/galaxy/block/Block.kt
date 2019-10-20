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

package one.oktw.galaxy.block

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.item.BlockItem
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.type.BlockType.DUMMY
import one.oktw.galaxy.block.util.BlockUtil

class Block(val type: BlockType = DUMMY) {
    val item = if (type.customModelData != null) BlockItem(type) else null

    suspend fun activate(world: ServerWorld, blockPos: BlockPos) =
        if (type.customModelData != null) {
            BlockUtil.placeAndRegisterBlock(world, this.item!!.createItemStack(), type, blockPos)
        } else {
            BlockUtil.registerBlock(world, type, blockPos)
        }
}
