package one.oktw.galaxy.galaxy.planet.gen

import one.oktw.galaxy.galaxy.planet.gen.populator.Spawn
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.world.gen.WorldGenerator
import org.spongepowered.api.world.gen.WorldGeneratorModifier
import org.spongepowered.api.world.storage.WorldProperties

class PlanetGenModifier : WorldGeneratorModifier {
    override fun modifyWorldGenerator(world: WorldProperties, settings: DataContainer, worldGenerator: WorldGenerator) {
        worldGenerator.populators.add(0, Spawn())
    }

    override fun getName() = "Planet"

    override fun getId() = "planet"
}
