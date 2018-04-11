package one.oktw.galaxy.galaxy.planet.gen.populator

import kotlinx.coroutines.experimental.runBlocking
import one.oktw.galaxy.Main
import one.oktw.galaxy.data.DataUUID
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
        val min = volume.blockMin
        val max = volume.blockMax

        val isSpawn = min.x <= spawn.x && spawn.x <= max.x && min.z <= spawn.z && spawn.z <= max.z
        if (!isSpawn) return

        var start = spawn
        for (y in 0..192) {
            if (volume.getBlockType(start.add(0, y, 0)) == BlockTypes.AIR) {
                start = start.add(0, y, 0)
                break
            }
        }

        // fix spawn pos
        world.properties.spawnPosition = start

        // clean
        for (x in start.x - 2..start.x + 2) {
            for (y in start.y - 1..start.y + 3) {
                for (z in start.z - 2..start.z + 2) {
                    volume.setBlockType(x, y, z, BlockTypes.AIR)
                }
            }
        }

        // 3x3 iron block
        for (x in start.x - 1..start.x + 1) {
            for (z in start.z - 1..start.z + 1) {
                volume.setBlockType(x, start.y - 1, z, BlockTypes.IRON_BLOCK)
            }
        }

        // pillar
        for (x in intArrayOf(start.x - 2, start.x + 2)) {
            for (z in Arrays.asList(start.z - 2, start.z + 2)) {
                for (y in start.y..start.y + 2) {
                    volume.setBlockType(x, y, z, BlockTypes.IRON_BLOCK)
                }
                volume.setBlockType(x, start.y - 1, z, BlockTypes.GOLD_BLOCK)
                volume.setBlockType(x, start.y + 3, z, BlockTypes.DIAMOND_BLOCK)
            }
        }

        // column
        val north = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.NORTH).get()
        val east = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.EAST).get()
        val south = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.SOUTH).get()
        val west = BlockTypes.QUARTZ_STAIRS.defaultState.with(Keys.DIRECTION, Direction.WEST).get()

        for (i in -1..1) {
            for (y in intArrayOf(-1, 3)) {
                volume.setBlock(start.add(i, y, 2), south)
                volume.setBlock(start.add(i, y, -2), north)
                volume.setBlock(start.add(2, y, i), east)
                volume.setBlock(start.add(-2, y, i), west)
            }
        }

        // top
        for (i in 0..1) {
            volume.setBlock(start.add(-i, 3, 1), north.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // 0,1 -1,1
            volume.setBlock(start.add(i, 3, -1), south.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // 0,-1 1,-1
            volume.setBlock(start.add(1, 3, i), west.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // 1,0 1,1
            volume.setBlock(start.add(-1, 3, -i), east.with(Keys.PORTION_TYPE, PortionTypes.TOP).get()) // -1,0, -1,-1
        }
        volume.setBlockType(start.add(0, 3, 0), BlockTypes.GLASS)

        // beacon
        volume.setBlockType(start, BlockTypes.BEACON)
        runBlocking {
            Main.galaxyManager.getPlanetFromWorld(world.uniqueId).await()?.uuid?.apply {
                volume.offer(start, DataUUID(this))
                volume.addScheduledUpdate(start, 0, 0)
            }
        }
    }
}
