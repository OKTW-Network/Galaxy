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

package one.oktw.galaxy.worldData

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk

interface ChunkDataProvider<Data> {
    fun createData(pos: ChunkPos): Data
    fun parseData(world: World, chunkTag: CompoundTag, nbt: CompoundTag): Data
    fun writeData(world: World, chunk: Chunk, data: Data, nbt: CompoundTag)
    fun beforeUnload(world: World, chunk: Chunk, data: Data)
    fun tick(world: World, chunk: Chunk, data: Data)
}
