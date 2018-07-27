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

        var pos = Vector3i(spawn.x, 0, spawn.z)
        for (y in minY..maxY) {
            if (volume.getBlockType(pos.add(0, y, 0)) == BlockTypes.AIR) {
                pos = pos.add(0, y, 0)
                break
            }
        }

        // fix spawn pos
        world.properties.spawnPosition = pos

        // fix world border center
        world.spawnLocation.run {
            world.worldBorder.setCenter(x, z)
            world.properties.setWorldBorderCenter(x, z)
        }
    }
}
