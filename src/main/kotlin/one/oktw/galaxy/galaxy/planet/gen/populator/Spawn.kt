package one.oktw.galaxy.galaxy.planet.gen.populator

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.block.enums.CustomBlocks.PLANET_TERMINAL
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import org.spongepowered.api.block.BlockTypes.*
import org.spongepowered.api.data.key.Keys.DIRECTION
import org.spongepowered.api.data.key.Keys.PORTION_TYPE
import org.spongepowered.api.data.type.PortionTypes.TOP
import org.spongepowered.api.util.Direction.*
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.Extent
import org.spongepowered.api.world.gen.Populator
import org.spongepowered.api.world.gen.PopulatorType
import org.spongepowered.api.world.gen.PopulatorTypes
import java.util.*

class Spawn : Populator {
    override fun getType(): PopulatorType = PopulatorTypes.GENERIC_OBJECT

    override fun populate(world: World, volume: Extent, random: Random) {
        val spawn = world.spawnLocation.blockPosition
        val min = volume.blockMin
        val max = volume.blockMax

        if (spawn.x !in min.x..max.x || spawn.z !in min.z..max.z) return

        // clean
        for (x in spawn.x - 2..spawn.x + 2) {
            for (y in spawn.y - 1..spawn.y + 3) {
                for (z in spawn.z - 2..spawn.z + 2) {
                    volume.setBlockType(x, y, z, AIR)
                }
            }
        }

        // 3x3 iron block
        for (x in spawn.x - 1..spawn.x + 1) {
            for (z in spawn.z - 1..spawn.z + 1) {
                volume.setBlockType(x, spawn.y - 1, z, IRON_BLOCK)
            }
        }

        // pillar
        for (x in intArrayOf(spawn.x - 2, spawn.x + 2)) {
            for (z in Arrays.asList(spawn.z - 2, spawn.z + 2)) {
                for (y in spawn.y..spawn.y + 2) {
                    volume.setBlockType(x, y, z, IRON_BLOCK)
                }
                volume.setBlockType(x, spawn.y - 1, z, GOLD_BLOCK)
                volume.setBlockType(x, spawn.y + 3, z, DIAMOND_BLOCK)
            }
        }

        // column
        val north = QUARTZ_STAIRS.defaultState.with(DIRECTION, NORTH).get()
        val east = QUARTZ_STAIRS.defaultState.with(DIRECTION, EAST).get()
        val south = QUARTZ_STAIRS.defaultState.with(DIRECTION, SOUTH).get()
        val west = QUARTZ_STAIRS.defaultState.with(DIRECTION, WEST).get()

        for (i in -1..1) {
            for (y in arrayOf(-1, 3)) {
                volume.setBlock(spawn.add(i, y, 2), south)
                volume.setBlock(spawn.add(i, y, -2), north)
                volume.setBlock(spawn.add(2, y, i), east)
                volume.setBlock(spawn.add(-2, y, i), west)
            }
        }

        // top
        for (i in 0..1) {
            volume.setBlock(spawn.add(-i, 3, 1), north.with(PORTION_TYPE, TOP).get()) // 0,1 -1,1
            volume.setBlock(spawn.add(i, 3, -1), south.with(PORTION_TYPE, TOP).get()) // 0,-1 1,-1
            volume.setBlock(spawn.add(1, 3, i), west.with(PORTION_TYPE, TOP).get()) // 1,0 1,1
            volume.setBlock(spawn.add(-1, 3, -i), east.with(PORTION_TYPE, TOP).get()) // -1,0, -1,-1
        }
        volume.setBlockType(spawn.add(0, 3, 0), GLASS)

        // beacon
        volume.setBlockType(spawn, BEACON)
        launch(serverThread) {
            galaxyManager.get(world).await()?.getPlanet(world)?.uuid?.apply {
                volume.offer(spawn, DataBlockType(PLANET_TERMINAL))
                volume.offer(spawn, DataUUID(this))
                volume.addScheduledUpdate(spawn, 0, 0)
            }
        }
    }
}
