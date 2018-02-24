package one.oktw.galaxy.manager

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
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
import kotlin.collections.HashMap

class ChunkLoaderManager {
    private val logger = main.logger
    private val ticketManager = Sponge.getServer().chunkTicketManager
    private val collection = databaseManager.database.getCollection("ChunkLoader", ChunkLoader::class.java)
    private val worldTickets: HashMap<UUID, ChunkTicketManager.LoadingTicket> = HashMap()

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
                main.logger.warn("ChunkLoader({}) level({} chunks) large then forge limit({} chunks)!", location.extent.toString(), chunkList.size, ticket.numChunks)
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
                worldTickets[chunkLoader.uuid] = loadChunk(
                        it.getLocation(chunkLoader.position.toVector3d()),
                        chunkLoader.level * 2 + 1
                )
            }

            logger.info("Loaded ChunkLoader at {}", it.position.toString())
        }
    }

    suspend fun addChunkLoader(location: Location<World>, level: Int): UUID {
        val chunkLoader = ChunkLoader(
                position = Position(planet = galaxyManager.getPlanetFromWorld(location.extent.uniqueId).await()?.uuid)
                        .fromPosition(location.position),
                level = level
        )
        launch { collection.insertOne(chunkLoader) }

        worldTickets[chunkLoader.uuid] = loadChunk(location, level * 2 + 1)

        logger.info("Added ChunkLoader at {}", location.extent.toString())
        return chunkLoader.uuid
    }

    fun getChunkLoader(uuid: UUID) = async {
        return@async collection.find(eq("uuid", uuid)).firstOrNull()
    }

    fun delChunkLoader(uuid: UUID) {
        worldTickets[uuid]?.release()
        launch { collection.deleteOne(eq("uuid", uuid)) }
    }

    fun changeRange(uuid: UUID, level: Int) = async {
        val chunkLoader = collection.find(eq("uuid", uuid)).firstOrNull() ?: return@async
        val planet = chunkLoader.position.planet?.let { galaxyManager.getPlanet(it).await() } ?: return@async

        chunkLoader.level = level
        collection.replaceOne(eq("uuid", chunkLoader.uuid), chunkLoader)

        worldTickets[uuid]?.release()
        PlanetHelper.loadPlanet(planet).ifPresent { loadChunk(it.getLocation(chunkLoader.position.toVector3d()), level * 2 + 1) }
    }

    fun loadForcedWorld() = runBlocking {
        logger.info("Loading world has ChunkLoader...")

        collection.find().forEach {
            val world = galaxyManager.getPlanet(
                    it.position.planet ?: return@forEach
            ).await()?.loadWorld()?.orElse(null) ?: return@forEach

            loadChunk(world.getLocation(it.position.toVector3d()), it.level * 2 + 1)
        }
    }
}
