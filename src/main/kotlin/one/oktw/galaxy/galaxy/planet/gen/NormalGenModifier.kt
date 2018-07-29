package one.oktw.galaxy.galaxy.planet.gen

import one.oktw.galaxy.galaxy.planet.gen.populator.Spawn
import one.oktw.galaxy.galaxy.planet.gen.populator.SpawnPosFix
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.world.gen.WorldGenerator
import org.spongepowered.api.world.gen.WorldGeneratorModifier
import org.spongepowered.api.world.storage.WorldProperties

class NormalGenModifier : WorldGeneratorModifier {
    override fun modifyWorldGenerator(world: WorldProperties, settings: DataContainer, worldGenerator: WorldGenerator) {
        worldGenerator.populators.add(0, SpawnPosFix())
        worldGenerator.populators.add(Spawn())
    }

    override fun getName() = "Planet"

    override fun getId() = "planet"
}
