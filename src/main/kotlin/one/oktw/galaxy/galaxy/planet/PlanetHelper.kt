package one.oktw.galaxy.galaxy.planet

import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.gen.PlanetGenModifier
import org.spongepowered.api.Sponge
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

        fun createPlanet(name: String): Planet {
            if (server.getWorldProperties(name).isPresent)
                throw IllegalArgumentException("World already exists")
            if (!name.matches(Regex("[a-z0-9]+", RegexOption.IGNORE_CASE)))
                throw IllegalArgumentException("Name only allow a~z and 0~9")

            val properties: WorldProperties
            val archetype = Sponge.getRegistry().getType(WorldArchetype::class.java, name).orElse(null)
                    ?: WorldArchetype.builder()
                        .generateSpawnOnLoad(false)
                        .loadsOnStartup(false)
                        .keepsSpawnLoaded(false)
                        .generatorModifiers(PlanetGenModifier())
                        .build(name, name)

            logger.info("Create World [{}]", name)

            try {
                properties = server.createWorldProperties(name, archetype)
                server.saveWorldProperties(properties)
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
