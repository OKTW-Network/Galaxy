package one.oktw.galaxy.galaxy.planet.gen.populator

import com.flowpowered.math.vector.Vector3i
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.Extent
import org.spongepowered.api.world.gen.Populator
import org.spongepowered.api.world.gen.PopulatorType
import java.util.*

class SpawnPosFix(private val minY: Int = 64, private val maxY: Int = 255) : Populator {
    override fun getType() = object : PopulatorType {
        override fun getTranslation() = object : Translation {
            override fun getId() = "spawn_position_fix"

            override fun get(locale: Locale) = "Spawn position fix"

            override fun get(locale: Locale, vararg args: Any) = "Spawn position fix"
        }

        override fun getName() = "Spawn position fix"

        override fun getId() = "spawn_position_fix"
    }

    override fun populate(world: World, volume: Extent, random: Random) {
        val spawn = world.spawnLocation.blockPosition

        if (spawn.x !in volume.blockMin.x..volume.blockMax.x || spawn.z !in volume.blockMin.z..volume.blockMax.z) return

        var currentBottom = 0
        var maxLength = 0
        // assume we start from a solid block
        var currentInAir = false
        var foundBottom = 0

        val pos = Vector3i(spawn.x, 0, spawn.z)

        for (y in minY..maxY) {
            if (volume.getBlockType(pos.add(0, y, 0)) == BlockTypes.AIR) {
                // if previous block is not air, then we st the new bottom
                if (!currentInAir) {
                    currentBottom = y
                }

                // this block is air
                currentInAir = true

                // if current air segment is longer than previous one
                if (y - currentBottom + 1 > maxLength) {
                    foundBottom = currentBottom
                    maxLength = y - currentBottom + 1
                }
            } else {
                // this block is not air
                currentInAir = false
            }
        }

        if (maxLength > 0) {
            // fix spawn pos
            world.properties.spawnPosition = pos.add(0, foundBottom, 0)
        } else {
            world.properties.spawnPosition = pos.add(0, minY, 0)
        }

        // fix world border center
        world.spawnLocation.run {
            world.worldBorder.setCenter(x, z)
            world.properties.setWorldBorderCenter(x, z)
        }
    }
}
