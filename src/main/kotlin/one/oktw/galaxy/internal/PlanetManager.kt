package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.types.Planet
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World
import org.spongepowered.api.world.WorldArchetypes
import org.spongepowered.api.world.storage.WorldProperties
import java.io.IOException
import java.io.UncheckedIOException
import java.util.*
import java.util.concurrent.CompletableFuture

internal class PlanetManager {
    companion object {
        private val logger = main.logger
        private val server = Sponge.getServer()

        fun createPlanet(name: String): Planet {
            val properties: WorldProperties
            logger.info("Create World [{}]", name)

            try {
                properties = server.createWorldProperties(name, WorldArchetypes.OVERWORLD)
                properties.setKeepSpawnLoaded(false)
                properties.setGenerateSpawnOnLoad(false)
                properties.setLoadOnStartup(false)
                server.saveWorldProperties(properties)
            } catch (e: IOException) {
                logger.error("Create world failed!", e)
                throw UncheckedIOException(e)
            }

            val planet = Planet(world = properties.uniqueId, name = name)
            return planet
        }

        fun removePlanet(worldUUID: UUID): CompletableFuture<Boolean>? {
            val properties: WorldProperties
            if (server.getWorldProperties(worldUUID).isPresent) {
                properties = server.getWorldProperties(worldUUID).get()
            } else {
                logger.error("Delete World [{}] failed: world not found", worldUUID.toString())
                return null
            }

            logger.info("Deleting World [{}]", properties.worldName)
            if (server.getWorld(worldUUID).isPresent) {
                val world = server.getWorld(worldUUID).get()
                world.players.parallelStream().forEach { player -> player.setLocationSafely(server.getWorld(server.defaultWorldName).get().spawnLocation) }
                server.unloadWorld(world)
            }

            return server.deleteWorld(properties)
        }

        fun loadPlanet(uuid: UUID): Optional<World> {
            return if (server.getWorldProperties(uuid).isPresent) {
                val worldProperties = server.getWorldProperties(uuid).get()
                worldProperties.setGenerateSpawnOnLoad(false)
                server.loadWorld(worldProperties)
            } else {
                Optional.empty()
            }
        }
    }
}
