package one.oktw.galaxy.galaxy.planet.gen.populator

import org.spongepowered.api.block.BlockTypes.AIR
import org.spongepowered.api.block.BlockTypes.STONE
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.Extent
import org.spongepowered.api.world.gen.Populator
import org.spongepowered.api.world.gen.PopulatorType
import org.spongepowered.api.world.gen.PopulatorTypes
import java.util.*

class FillSpawn : Populator {
    override fun getType(): PopulatorType = PopulatorTypes.GENERIC_OBJECT

    override fun populate(world: World, volume: Extent, random: Random) {
        val spawn = world.spawnLocation.blockPosition
        val min = volume.blockMin
        val max = volume.blockMax

        if (spawn.x !in min.x..max.x || spawn.z !in min.z..max.z) return

        var start = spawn
        for (y in 0..192) {
            if (volume.getBlockType(start.add(0, y, 0)) == AIR) {
                start = start.add(0, y, 0)
                break
            }
        }

        // fix spawn pos
        world.properties.spawnPosition = start

        // fix world border center
        world.spawnLocation.run {
            world.worldBorder.setCenter(x, z)
            world.properties.setWorldBorderCenter(x, z)
        }

        // fill
        for (x in start.x - 2..start.x + 2) {
            for (y in start.y - 1..start.y + 3) {
                for (z in start.z - 2..start.z + 2) {
                    volume.setBlockType(x, y, z, STONE)
                }
            }
        }
    }
}
