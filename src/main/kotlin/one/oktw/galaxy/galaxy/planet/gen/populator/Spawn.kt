/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

package one.oktw.galaxy.galaxy.planet.gen.populator

import one.oktw.galaxy.block.enums.CustomBlocks.PLANET_TERMINAL
import one.oktw.galaxy.data.DataBlockType
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.PortionTypes
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.Extent
import org.spongepowered.api.world.gen.Populator
import org.spongepowered.api.world.gen.PopulatorType
import java.util.*

class Spawn : Populator {
    override fun getType() = object : PopulatorType {
        override fun getTranslation() = object : Translation {
            override fun getId() = "spawn"

            override fun get(locale: Locale) = "Spawn"

            override fun get(locale: Locale, vararg args: Any) = "Spawn"
        }

        override fun getName() = "Spawn"

        override fun getId() = "spawn"
    }

    override fun populate(world: World, volume: Extent, random: Random) {
        val spawn = world.spawnLocation.blockPosition

        if (spawn.x !in volume.blockMin.x..volume.blockMax.x || spawn.z !in volume.blockMin.z..volume.blockMax.z) return

        // clean
        for (x in spawn.x - 2..spawn.x + 2) {
            for (y in spawn.y - 1..spawn.y + 3) {
                for (z in spawn.z - 2..spawn.z + 2) {
                    volume.setBlockType(x, y, z, BlockTypes.AIR)
                }
            }
        }

        // 3x3 iron block
        for (x in spawn.x - 1..spawn.x + 1) {
            for (z in spawn.z - 1..spawn.z + 1) {
                volume.setBlockType(x, spawn.y - 1, z, BlockTypes.IRON_BLOCK)
            }
        }

        // pillar
        for (x in intArrayOf(spawn.x - 2, spawn.x + 2)) {
            for (z in Arrays.asList(spawn.z - 2, spawn.z + 2)) {
                for (y in spawn.y..spawn.y + 2) {
                    volume.setBlockType(x, y, z, BlockTypes.IRON_BLOCK)
                }
                volume.setBlockType(x, spawn.y - 1, z, BlockTypes.GOLD_BLOCK)
                volume.setBlockType(x, spawn.y + 3, z, BlockTypes.DIAMOND_BLOCK)
            }
        }

        // column
        val north = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.NORTH).get()
        val east = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.EAST).get()
        val south = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.SOUTH).get()
        val west = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.WEST).get()

        for (i in -1..1) {
            for (y in intArrayOf(-1, 3)) {
                volume.setBlock(spawn.add(i, y, 2), south)
                volume.setBlock(spawn.add(i, y, -2), north)
                volume.setBlock(spawn.add(2, y, i), east)
                volume.setBlock(spawn.add(-2, y, i), west)
            }
        }

        // top
        for (i in 0..1) {
            volume.setBlock(spawn.add(-i, 3, 1), north.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // 0,1 -1,1
            volume.setBlock(spawn.add(i, 3, -1), south.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // 0,-1 1,-1
            volume.setBlock(spawn.add(1, 3, i), west.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // 1,0 1,1
            volume.setBlock(spawn.add(-1, 3, -i), east.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // -1,0, -1,-1
        }
        volume.setBlockType(spawn.add(0, 3, 0), BlockTypes.GLASS)

        // beacon
        volume.setBlockType(spawn, BlockTypes.BEACON)
        volume.offer(spawn, DataBlockType(PLANET_TERMINAL))
        volume.addScheduledUpdate(spawn, 0, 0)
    }
}
