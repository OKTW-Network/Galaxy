package one.oktw.galaxy.manager

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.launch
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
            reloadChunkLoader(world)
        }
    }

    private fun loadChunk(location: Location<World>, range: Short): ChunkTicketManager.LoadingTicket {
        val ticket = ticketManager.createTicket(main, location.extent).get()

        launch {
            val chunkPos = location.chunkPosition
            val chunkList = HashSet<Vector3i>()
            (chunkPos.x - range..chunkPos.x + range).forEach { x ->
                (chunkPos.z - range..chunkPos.z + range).forEach { z ->
                    chunkList.add(Vector3i(x, 0, z))
                }
            }
            if (chunkList.size > ticket.numChunks) {
                main.logger.warn("ChunkLoader({}) range({} chunks) large then forge limit({} chunks)!", location.extent.toString(), chunkList.size, ticket.numChunks)
            }
            chunkList.parallelStream().forEach(ticket::forceChunk)
        }

        return ticket
    }

    private fun reloadChunkLoader(world: World) {
        logger.info("Reloading ChunkLoader in \"{}\" ...", world.name)

        val planet = galaxyManager.getPlanetFromWorld(world.uniqueId) ?: return
        launch {
            collection.find(eq("position.planet", planet.uuid)).forEach {
                val chunkLoader = it
                planet.getWorld().ifPresent {
                    worldTickets[chunkLoader.uuid] = loadChunk(
                            it.getLocation(chunkLoader.position.toVector3d()),
                            chunkLoader.range
                    )
                }

                logger.info("Loaded ChunkLoader at {}", it.position.toString())
            }
        }
    }

    fun addChunkLoader(location: Location<World>, range: Short): UUID {
        val chunkLoader = ChunkLoader(
                position = Position(
                        location.x,
                        location.y,
                        location.z,
                        galaxyManager.getPlanetFromWorld(location.extent.uniqueId)?.uuid
                ),
                range = range
        )
        launch { collection.insertOne(chunkLoader) }

        worldTickets[chunkLoader.uuid] = loadChunk(location, range)

        logger.info("Added ChunkLoader at {}", location.extent.toString())
        return chunkLoader.uuid
    }

    fun delChunkLoader(uuid: UUID) {
        worldTickets[uuid]?.release()
        launch { collection.deleteOne(eq("uuid", uuid)) }
    }

    fun changeRange(uuid: UUID, range: Short) {
        val chunkLoader = collection.find(eq("uuid", uuid)).firstOrNull() ?: return
        val planet = galaxyManager.getPlanet(chunkLoader.position.planet!!) ?: return

        chunkLoader.range = range
        collection.replaceOne(eq("uuid", chunkLoader.uuid), chunkLoader)

        worldTickets[uuid]?.release()
        PlanetHelper.loadPlanet(planet).ifPresent { loadChunk(it.getLocation(chunkLoader.position.toVector3d()), range) }
    }

    fun loadForcedWorld() {
        logger.info("Loading world has ChunkLoader...")

        collection.find().forEach {
            val world = galaxyManager.getPlanet(
                    it.position.planet ?: return@forEach
            )?.getWorld()?.orElse(null) ?: return@forEach

            launch { loadChunk(world.getLocation(it.position.toVector3d()), it.range) }
        }
    }
}
