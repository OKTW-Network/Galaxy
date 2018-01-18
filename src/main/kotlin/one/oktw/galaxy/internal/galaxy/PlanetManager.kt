package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.galaxy.SecurityLevel.VISIT
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World
import org.spongepowered.api.world.WorldArchetypes
import org.spongepowered.api.world.storage.WorldProperties
import java.io.IOException
import java.io.UncheckedIOException
import java.util.*

class PlanetManager {
    companion object {
        private val logger = main.logger
        private val planetCollection = databaseManager.database.getCollection("Planet")
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
                    .append("Security", VISIT.level)

            planetCollection.insertOne(worldInfo)
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
                world.players.parallelStream().forEach { player -> player.setLocationSafely(server.getWorld(server.defaultWorldName).get().spawnLocation) }
                server.unloadWorld(world)
            }

            server.deleteWorld(properties).get()
            launch { planetCollection.deleteOne(eq("UUID", uuid)) }
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
