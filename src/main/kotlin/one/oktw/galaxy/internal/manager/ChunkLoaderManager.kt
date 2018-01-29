package one.oktw.galaxy.internal.manager

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.Main.Companion.main
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.ChunkTicketManager
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.World
import java.util.*
import kotlin.collections.HashMap

class ChunkLoaderManager {
    private val logger = main.logger
    private val ticketManager = Sponge.getServer().chunkTicketManager
    private val chunkLoaderCollection = databaseManager.database.getCollection("ChunkLoader")
    private val worldTickets: HashMap<UUID, HashMap<Vector3i, ChunkTicketManager.LoadingTicket>> = HashMap()

    init {
        ticketManager.registerCallback(main) { tickets, world ->
            tickets.forEach { it.release() }
            reloadChunkLoader(world)
        }
    }

    fun addChunkLoader(locatableBlock: LocatableBlock, range: Short) {
        val document = Document("World", locatableBlock.world.uniqueId)
                .append("Location", Document("x", locatableBlock.position.x)
                        .append("y", locatableBlock.position.y)
                        .append("z", locatableBlock.position.z))
                .append("Range", range)
        launch { chunkLoaderCollection.insertOne(document) }
        val ticket = ticketManager.createTicket(main, locatableBlock.world).get()
        val chunkPos = locatableBlock.location.chunkPosition
        val chunkList = HashSet<Vector3i>()
        (chunkPos.x - range..chunkPos.x + range).forEach { x ->
            (chunkPos.z - range..chunkPos.z + range).forEach { z ->
                chunkList.add(Vector3i(x, 0, z))
            }
        }
        if (chunkList.size > ticket.numChunks) {
            main.logger.warn("ChunkLoader({}) range({} chunks) large then forge limit({} chunks)!", locatableBlock.location.toString(), chunkList.size, ticket.numChunks)
        }
        chunkList.parallelStream().forEach(ticket::forceChunk)
        worldTickets.getOrDefault(locatableBlock.world.uniqueId, HashMap())
        logger.info("Added ChunkLoader at {}", locatableBlock.location.toString())
    }

    fun delChunkLoader(locatableBlock: LocatableBlock) {
        val filter = Document("x", locatableBlock.position.x).append("y", locatableBlock.position.y).append("z", locatableBlock.position.z)
        worldTickets[locatableBlock.world.uniqueId]?.get(locatableBlock.position)?.release()
        launch { chunkLoaderCollection.deleteOne(eq("Location", filter)) }
    }

    fun changeRange(locatableBlock: LocatableBlock, range: Short) {
        delChunkLoader(locatableBlock)
        addChunkLoader(locatableBlock, range)
    }

    fun loadForcedWorld() {
        logger.info("Loading world has ChunkLoader...")
        chunkLoaderCollection.distinct("World", UUID::class.java).forEach {
            Sponge.getServer().loadWorld(it).ifPresent { reloadChunkLoader(it) }
        }
    }

    fun reloadChunkLoader(world: World) {
        logger.info("Reloading ChunkLoader in \"{}\" ...", world.name)
        worldTickets[world.uniqueId] = HashMap()
        launch {
            chunkLoaderCollection.find(eq("World", world.uniqueId)).forEach {
                val ticket = ticketManager.createTicket(main, world).get()
                val location = it["Location"] as Document
                val blockPos = world.getLocation(location["x"] as Int, location["y"] as Int, location["z"] as Int)
                val chunkPos = blockPos.chunkPosition
                val chunkList = HashSet<Vector3i>()
                val range = it["Range"] as Int

                // Generate force load chunks list
                (chunkPos.x - range..chunkPos.x + range).forEach { x ->
                    (chunkPos.z - range..chunkPos.z + range).forEach { z ->
                        chunkList.add(Vector3i(x, 0, z))
                    }
                }

                // Warn if out of limit
                if (chunkList.size > ticket.numChunks) {
                    main.logger.warn("ChunkLoader({}) range({} chunks) large then forge limit({} chunks)!", blockPos.toString(), chunkList.size, ticket.numChunks)
                }

                // Force load chunks
                chunkList.parallelStream().forEach { ticket.forceChunk(it) }
                worldTickets[world.uniqueId]!![blockPos.biomePosition] = ticket
                logger.info("Loaded ChunkLoader at {}", blockPos.position.toString())
            }
        }
    }
}
