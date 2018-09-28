package one.oktw.galaxy.galaxy.planet

import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NORMAL
import one.oktw.galaxy.galaxy.planet.gen.NetherGenModifier
import one.oktw.galaxy.galaxy.planet.gen.NormalGenModifier
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.DimensionTypes.NETHER
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
        private val normalArchetype = Sponge.getRegistry().getType(WorldArchetype::class.java, "planet").orElse(null) ?: WorldArchetype.builder()
            .generateSpawnOnLoad(false)
            .loadsOnStartup(false)
            .keepsSpawnLoaded(false)
            .generatorModifiers(NormalGenModifier())
            .randomSeed()
            .build("planet", "planet")
        private val netherArchetype = Sponge.getRegistry().getType(WorldArchetype::class.java, "planet_nether").orElse(null) ?: WorldArchetype.builder()
            .dimension(NETHER)
            .generateSpawnOnLoad(false)
            .loadsOnStartup(false)
            .keepsSpawnLoaded(false)
            .generatorModifiers(NetherGenModifier())
            .randomSeed()
            .build("planet_nether", "planet_nether")

        suspend fun createPlanet(name: String, type: PlanetType = NORMAL) = withContext(serverThread) {
            if (server.getWorldProperties(name).isPresent)
                throw IllegalArgumentException("World already exists")
            if (!name.matches(Regex("[a-z0-9\\-_]+", RegexOption.IGNORE_CASE)))
                throw IllegalArgumentException("Name contains characters that are not allowed")

            val properties: WorldProperties
            val archetype = when (type) {
                PlanetType.NORMAL -> normalArchetype
                PlanetType.NETHER -> netherArchetype
                PlanetType.END -> TODO()
            }

            logger.info("Create World [{}]", name)

            try {
                properties = server.createWorldProperties(name, archetype).also {
                    server.loadWorld(it).get().apply {
                        spawnLocation.chunkPosition.apply {
                            for (x in x - 1..x + 1) {
                                for (z in z - 1..z + 1) {
                                    loadChunk(x, 0, z, true)
                                }
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                logger.error("Create world failed!", e)
                throw UncheckedIOException(e)
            }

            return@withContext Planet(world = properties.uniqueId, name = name, type = type)
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
                world.players.forEach { it.setLocationSafely(server.getWorld(server.defaultWorldName).get().spawnLocation) }
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
                server.loadWorld(it).orElse(null)?.apply {
                    worldBorder.setDiameter(planet.size * 16.0, 0)
                }
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
