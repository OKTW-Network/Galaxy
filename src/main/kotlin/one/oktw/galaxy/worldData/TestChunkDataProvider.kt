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
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.WorldChunk

class TestChunkDataProvider private constructor(): ChunkDataProvider<TestData> {
    companion object {
        val instance = TestChunkDataProvider()
    }

    private var counter = 0

    override fun createData(pos: ChunkPos): TestData {
        return TestData()
    }

    override fun parseData(world: World, chunkTag: CompoundTag, nbt: CompoundTag): TestData {
        return TestData(nbt)
    }

    override fun writeData(world: World, chunk: Chunk, data: TestData, nbt: CompoundTag) {
        data.writeNbt(nbt)
    }

    override fun tick(world: World, chunk: WorldChunk, randomTickSpeed: Int, data: TestData) {
        counter++
        if (counter % 5 == 0) {
            data.positions.forEach {
                (world as ServerWorld).spawnParticles(
                    ParticleTypes.EXPLOSION,
                    it.first.toDouble() + 0.5,
                    it.second.toDouble() + 0.5,
                    it.third.toDouble() + 0.5,
                    10,
                    0.0,
                    0.0,
                    0.0,
                    0.0
                )
            }
        }
    }
}
