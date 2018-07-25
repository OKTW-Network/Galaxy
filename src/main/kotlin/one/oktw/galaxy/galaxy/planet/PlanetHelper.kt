package one.oktw.galaxy.galaxy.planet

import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NORMAL
import one.oktw.galaxy.galaxy.planet.gen.PlanetGenModifier
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.DimensionTypes.*
import org.spongepowered.api.world.World
import org.spongepowered.api.world.WorldArchetype
import org.spongepowered.api.world.storage.WorldProperties
import java.io.IOException
import java.io.UncheckedIOException
import java.util.*
import java.util.concurrent.CompletableFuture

class PlanetHelper {
    companion object {
        private val logger = main.logger
        private val server = Sponge.getServer()

        suspend fun createPlanet(name: String, type: PlanetType = NORMAL): Planet {
            if (server.getWorldProperties(name).isPresent)
                throw IllegalArgumentException("World already exists")
            if (!name.matches(Regex("[a-z0-9\\-_]+", RegexOption.IGNORE_CASE)))
                throw IllegalArgumentException("Name contains characters that are not allowed")

            val properties: WorldProperties
            val archetype = Sponge.getRegistry().getType(WorldArchetype::class.java, "planet").orElse(null)
                ?: WorldArchetype.builder()
                    .dimension(
                        when (type) {
                            PlanetType.NORMAL -> OVERWORLD
                            PlanetType.NETHER -> NETHER
                            PlanetType.END -> THE_END
                        }
                    )
                    .generateSpawnOnLoad(false)
                    .loadsOnStartup(false)
                    .keepsSpawnLoaded(false)
                    .generatorModifiers(PlanetGenModifier())
                    .build("planet", "planet")

            logger.info("Create World [{}]", name)

            try {
                properties = withContext(serverThread) { server.createWorldProperties(name, archetype) }
                withContext(serverThread) { server.saveWorldProperties(properties) }
            } catch (e: IOException) {
                logger.error("Create world failed!", e)
                throw UncheckedIOException(e)
            }

            return Planet(world = properties.uniqueId, name = name)
        }

        fun removePlanet(worldUUID: UUID): CompletableFuture<Boolean> {
            val properties: WorldProperties
            if (server.getWorldProperties(worldUUID).isPresent) {
                properties = server.getWorldProperties(worldUUID).get()
            } else {
                return CompletableFuture.completedFuture(true)
            }

            logger.info("Deleting World [{}]", properties.worldName)
            if (server.getWorld(worldUUID).isPresent) {
                val world = server.getWorld(worldUUID).get()
                world.players.parallelStream()
                    .forEach { it.setLocationSafely(server.getWorld(server.defaultWorldName).get().spawnLocation) }
                server.unloadWorld(world)
            }

            return server.deleteWorld(properties)
        }

        suspend fun loadPlanet(planet: Planet): World? = withContext(serverThread) {
            server.getWorldProperties(planet.world).orElse(null)?.let {
                planet.lastTime = Date()
                it.worldBorderDiameter = (planet.size * 16).toDouble()
                it.setGenerateSpawnOnLoad(false)
                server.saveWorldProperties(it)
                server.loadWorld(it).orElse(null)
            }
        }

        fun updatePlanet(planet: Planet) {
            server.getWorldProperties(planet.world).ifPresent {
                it.worldBorderDiameter = (planet.size * 16).toDouble()
                server.saveWorldProperties(it)
            }
        }
    }
}
