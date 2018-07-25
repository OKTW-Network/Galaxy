package one.oktw.galaxy.machine.chunkloader

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.awaitFirstOrNull
import kotlinx.coroutines.experimental.reactive.consumeEach
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Position
import one.oktw.galaxy.galaxy.planet.data.extensions.loadWorld
import one.oktw.galaxy.galaxy.planet.data.extensions.toVector3d
import one.oktw.galaxy.internal.DatabaseManager.Companion.database
import one.oktw.galaxy.machine.chunkloader.data.ChunkLoader
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.ChunkTicketManager
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ChunkLoaderManager {
    private val logger = main.logger
    private val ticketManager = Sponge.getServer().chunkTicketManager
    private val collection = database.getCollection("ChunkLoader", ChunkLoader::class.java)
    private val worldTickets: ConcurrentHashMap<UUID, ChunkTicketManager.LoadingTicket> = ConcurrentHashMap()

    init {
        ticketManager.registerCallback(main) { tickets, world ->
            tickets.forEach { it.release() }
            reload(world)
        }

        logger.info("Loading world has ChunkLoader...")

        launch {
            collection.find().consumeEach {
                val planet = it.position.planet?.let { galaxyManager.get(planet = it)?.getPlanet(it) }

                if (planet == null) {
                    delete(it.uuid)
                    return@consumeEach
                }

                val range = (it.upgrade.maxBy { it.level }?.level ?: 0) * 2 + 1
                val world = planet.loadWorld() ?: return@launch

                worldTickets[it.uuid] = loadChunk(world.getLocation(it.position.toVector3d()), range)
            }
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

    private fun reload(world: World) = launch {
        logger.info("Reloading ChunkLoader in \"{}\" ...", world.name)

        val planet = galaxyManager.get(world)?.getPlanet(world) ?: return@launch

        collection.find(eq("position.planet", planet.uuid)).consumeEach { chunkLoader ->
            val range = (chunkLoader.upgrade.maxBy { it.level }?.level ?: 0) * 2 + 1

            worldTickets[chunkLoader.uuid] = loadChunk(world.getLocation(chunkLoader.position.toVector3d()), range)

            logger.info("Loaded ChunkLoader at {}", chunkLoader.position.toString())
        }
    }

    suspend fun add(location: Location<World>): ChunkLoader {
        val chunkLoader = ChunkLoader(position = Position(location.position, location.extent.let { galaxyManager.get(it)?.getPlanet(it)?.uuid!! }))
        collection.insertOne(chunkLoader).awaitFirstOrNull()

        worldTickets[chunkLoader.uuid] = loadChunk(location, 1)

        logger.info("Added ChunkLoader at {}", location.extent.toString())
        return chunkLoader
    }

    fun get(uuid: UUID) = async {
        return@async collection.find(eq("uuid", uuid)).awaitFirstOrNull()
    }

    suspend fun delete(uuid: UUID) {
        worldTickets[uuid]?.release()
        collection.deleteOne(eq("uuid", uuid)).awaitFirstOrNull()
    }

    fun update(chunkLoader: ChunkLoader, reload: Boolean = false) = launch {
        collection.replaceOne(eq("uuid", chunkLoader.uuid), chunkLoader).awaitFirstOrNull()

        if (reload) {
            val planet = chunkLoader.position.planet?.let { galaxyManager.get(planet = it)?.getPlanet(it) } ?: return@launch
            val range = chunkLoader.upgrade.maxBy { it.level }?.level ?: 0

            worldTickets[chunkLoader.uuid]?.release()
            PlanetHelper.loadPlanet(planet)?.let {
                worldTickets[chunkLoader.uuid] = loadChunk(it.getLocation(chunkLoader.position.toVector3d()), range * 2 + 1)
            }
        }
    }
}
