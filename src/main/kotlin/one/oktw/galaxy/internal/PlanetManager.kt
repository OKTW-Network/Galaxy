package one.oktw.galaxy.internal

import com.mongodb.client.model.Filters.eq
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.SecurityLevel.VISIT
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World
import org.spongepowered.api.world.WorldArchetypes
import org.spongepowered.api.world.storage.WorldProperties
import java.io.IOException
import java.io.UncheckedIOException
import java.util.*
import java.util.concurrent.ExecutionException

class PlanetManager {
    companion object {
        private val logger = main.logger
        private val planets = Main.databaseManager.database.getCollection("Planet")
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

            val worldInfo = Document("UUID", properties.uniqueId)
                    .append("Name", name)
                    .append("Size", 32)
                    .append("Security", VISIT)

            planets.insertOne(worldInfo)
            return Planet(properties.uniqueId)
        }

        fun removePlanet(uuid: UUID) {
            val properties: WorldProperties
            if (server.getWorldProperties(uuid).isPresent) {
                properties = server.getWorldProperties(uuid).get()
            } else {
                logger.error("Delete World [{}] failed: world not found", uuid.toString())
                return
            }

            logger.info("Deleting World [{}]", properties.worldName)
            if (server.getWorld(uuid).isPresent) {
                val world = server.getWorld(uuid).get()
                world.players.forEach { player -> player.setLocationSafely(server.getWorld(server.defaultWorldName).get().spawnLocation) }
                server.unloadWorld(world)
            }

            try {
                server.deleteWorld(properties).get()
            } catch (e: InterruptedException) {
                logger.error("Delete world failed!", e)
                return
            } catch (e: ExecutionException) {
                logger.error("Delete world failed!", e)
                return
            }

            planets.deleteOne(eq("UUID", uuid))
        }

        internal fun loadPlanet(uuid: UUID): Optional<World> {
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
