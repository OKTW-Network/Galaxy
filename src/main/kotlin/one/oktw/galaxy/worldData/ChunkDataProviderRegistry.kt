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
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ProtoChunk
import java.util.concurrent.ConcurrentHashMap

class ChunkDataProviderRegistry private constructor() {
    companion object {
        const val DATA_ROOT_NAME = "GalaxyData"
        val instance = ChunkDataProviderRegistry()
    }

    private val nameProviderMap = ConcurrentHashMap<String, ChunkDataProvider<Any>>()
    private val providerNameMap = ConcurrentHashMap<ChunkDataProvider<Any>, String>()

    val providers: Iterable<ChunkDataProvider<Any>>
        get () {
            return nameProviderMap.elements().toList()
        }

    fun register(name: String, provider: ChunkDataProvider<out Any>) {
        if (nameProviderMap.containsKey(name)) {
            throw Exception("$name already registered")
        }

        @Suppress("UNCHECKED_CAST")
        provider as ChunkDataProvider<Any>

        nameProviderMap[name] = provider
        providerNameMap[provider] = name
    }

    fun getRegisteredName(provider: ChunkDataProvider<Any>): String? {
        return providerNameMap[provider]
    }

    fun <T> createData(pos: ChunkPos, provider: ChunkDataProvider<T>): T {
        return provider.createData(pos)
    }

    fun parseData(world: ServerWorld, pos: ChunkPos, tag: CompoundTag, chunk: ProtoChunk) {
        if (tag.contains(DATA_ROOT_NAME)) {
            val root = tag.getCompound(DATA_ROOT_NAME)
            nameProviderMap.elements().toList().forEach {
                val name = providerNameMap[it]

                if (root.contains(name)) {
                    (chunk as ExtendedChunk).setData(it, it.parseData(world, tag, root.getCompound(name)))
                } else {
                    (chunk as ExtendedChunk).setData(it, it.createData(pos))
                }
            }
        } else {
            nameProviderMap.elements().toList().forEach {
                (chunk as ExtendedChunk).setData(it, it.createData(pos))
            }
        }
    }

    fun writeData(world: ServerWorld, chunk: Chunk, tag: CompoundTag) {
        val root = CompoundTag()
        tag.put(DATA_ROOT_NAME, root)

        nameProviderMap.elements().toList().forEach {
            val name = providerNameMap[it]
            val sub = CompoundTag()
            root.put(name, sub)
            it.writeData(world, chunk, (chunk as ExtendedChunk).getData(it), sub)
        }
    }
}
