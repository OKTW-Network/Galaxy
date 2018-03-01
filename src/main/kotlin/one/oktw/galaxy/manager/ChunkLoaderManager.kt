package one.oktw.galaxy.manager

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.helper.PlanetHelper
import one.oktw.galaxy.types.ChunkLoader
import one.oktw.galaxy.types.Position
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.ChunkTicketManager
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ChunkLoaderManager {
    private val logger = main.logger
    private val ticketManager = Sponge.getServer().chunkTicketManager
    private val collection = databaseManager.database.getCollection("ChunkLoader", ChunkLoader::class.java)
    private val worldTickets: ConcurrentHashMap<UUID, ChunkTicketManager.LoadingTicket> = ConcurrentHashMap()

    init {
        ticketManager.registerCallback(main) { tickets, world ->
            tickets.forEach { it.release() }
            runBlocking { reloadChunkLoader(world) }
        }
    }

    private fun loadChunk(location: Location<World>, range: Int): ChunkTicketManager.LoadingTicket {
        val ticket = ticketManager.createTicket(main, location.extent).get()

        launch {
            val chunkPos = location.chunkPosition
            val chunkList = HashSet<Vector3i>()
            for (x in chunkPos.x - range..chunkPos.x + range) {
                for (z in chunkPos.z - range..chunkPos.z + range) {
                    chunkList.add(Vector3i(x, 0, z))
                }
            }
            if (chunkList.size > ticket.numChunks) {
                main.logger.warn(
                    "ChunkLoader({}) level({} chunks) large then forge limit({} chunks)!",
                    location.extent.toString(),
                    chunkList.size,
                    ticket.numChunks
                )
            }
            chunkList.parallelStream().forEach(ticket::forceChunk)
        }

        return ticket
    }

    private suspend fun reloadChunkLoader(world: World) {
        logger.info("Reloading ChunkLoader in \"{}\" ...", world.name)

        val planet = galaxyManager.getPlanetFromWorld(world.uniqueId).await() ?: return
        collection.find(eq("position.planet", planet.uuid)).forEach {
            val chunkLoader = it
            planet.loadWorld().ifPresent {
                val range = (chunkLoader.upgrade.maxBy { it.level }?.level ?: 0) * 2 + 1

                worldTickets[chunkLoader.uuid] = loadChunk(it.getLocation(chunkLoader.position.toVector3d()), range)
            }

            logger.info("Loaded ChunkLoader at {}", it.position.toString())
        }
    }

    suspend fun addChunkLoader(location: Location<World>): ChunkLoader {
        val chunkLoader = ChunkLoader(
            position = Position(planet = galaxyManager.getPlanetFromWorld(location.extent.uniqueId).await()?.uuid)
                .fromPosition(location.position)
        )
        launch { collection.insertOne(chunkLoader) }

        worldTickets[chunkLoader.uuid] = loadChunk(location, 1)

        logger.info("Added ChunkLoader at {}", location.extent.toString())
        return chunkLoader
    }

    fun get(uuid: UUID) = async {
        return@async collection.find(eq("uuid", uuid)).firstOrNull()
    }

    fun delete(uuid: UUID) {
        worldTickets[uuid]?.release()
        launch { collection.deleteOne(eq("uuid", uuid)) }
    }

    fun updateChunkLoader(chunkLoader: ChunkLoader, reload: Boolean = false) = async {
        collection.replaceOne(eq("uuid", chunkLoader.uuid), chunkLoader)

        if (reload) {
            val planet = chunkLoader.position.planet?.let { galaxyManager.getPlanet(it).await() } ?: return@async
            val range = chunkLoader.upgrade.maxBy { it.level }?.level ?: 0

            worldTickets[chunkLoader.uuid]?.release()
            PlanetHelper.loadPlanet(planet).ifPresent {
                worldTickets[chunkLoader.uuid] =
                        loadChunk(it.getLocation(chunkLoader.position.toVector3d()), range * 2 + 1)
            }
        }
    }

    fun loadForcedWorld() = runBlocking {
        logger.info("Loading world has ChunkLoader...")

        collection.find().forEach {
            launch {
                val planet = galaxyManager.getPlanet(
                    it.position.planet ?: return@launch
                ).await() ?: return@launch
                val range = (it.upgrade.maxBy { it.level }?.level ?: 0) * 2 + 1
                val world = withContext(this@runBlocking.coroutineContext) { planet.loadWorld().orElse(null) }
                        ?: return@launch

                worldTickets[it.uuid] = loadChunk(world.getLocation(it.position.toVector3d()), range)
            }
        }
    }
}
