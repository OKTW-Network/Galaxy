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
import net.minecraft.world.chunk.WorldChunk

data class A(val test: Long)

class TestChunkDataProvider: ChunkDataProvider<A> {
    override fun createData(pos: ChunkPos): A {
        return A(1)
    }

    override fun parseData(world: World, chunkTag: CompoundTag, nbt: CompoundTag): A {
        println("parse ${nbt.getLong("a")}")
        return A(nbt.getLong("a") + 1)
    }

    override fun writeData(world: World, chunk: Chunk, data: A, nbt: CompoundTag) {
        println("write ${data.test}")
        nbt.putLong("a", data.test)
    }

    override fun beforeUnload(world: World, chunk: WorldChunk, data: A) {
    }

    override fun afterLoad(world: World, chunk: WorldChunk, data: A) {
    }

    override fun tick(world: World, chunk: WorldChunk, randomTickSpeed: Int, data: A) {
        (chunk as ExtendedChunk).setData(this, A(data.test + 1))
        chunk.markDirty()
    }
}
