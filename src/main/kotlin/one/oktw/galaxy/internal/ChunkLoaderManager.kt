package one.oktw.galaxy.internal

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.Main.Companion.main
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.ChunkTicketManager
import org.spongepowered.api.world.LocatableBlock
import java.util.*
import kotlin.collections.HashMap

class ChunkLoaderManager {
    private val logger = main.logger
    private val ticketManager = Sponge.getServer().chunkTicketManager
    private val database = databaseManager.database.getCollection("ChunkLoader")
    private val worldTickets: HashMap<UUID, HashMap<Vector3i, ChunkTicketManager.LoadingTicket>> = HashMap()

    init {
        ticketManager.registerCallback(main) { tickets, world ->
            logger.info("Reloading {} ChunkLoader...", world.name)
            worldTickets[world.uniqueId] = HashMap()
            launch {
                val ticketIterator = tickets.iterator()
                database.find(eq("World", world.uniqueId)).forEach {
                    val ticket = if (ticketIterator.hasNext()) ticketIterator.next() else ticketManager.createTicket(main, world).get()
                    val location = it["Location"] as Document
                    val blockPos = world.getLocation(location["x"] as Int, location["y"] as Int, location["z"] as Int)
                    val chunkPos = blockPos.chunkPosition
                    val chunkList = HashSet<Vector3i>()
                    val range = it["Range"] as Int
                    (chunkPos.x - range..chunkPos.x + range).forEach { x ->
                        (chunkPos.z - range..chunkPos.z + range).forEach { z ->
                            chunkList.add(Vector3i(x, 0, z))
                        }
                    }
                    if (chunkList.size > ticket.numChunks) {
                        main.logger.warn("ChunkLoader({}) range({} chunks) large then forge limit({} chunks)!", blockPos.toString(), chunkList.size, ticket.numChunks)
                    }
                    chunkList.parallelStream().forEach { ticket.forceChunk(it) }
                    worldTickets[world.uniqueId]!![blockPos.biomePosition] = ticket
                    logger.info("Loaded ChunkLoader at {}", location.toString())
                }
            }
        }
    }

    fun addChunkLoader(locatableBlock: LocatableBlock, range: Short) {
        val document = Document("World", locatableBlock.world.uniqueId)
                .append("Location", Document("x", locatableBlock.position.x)
                        .append("y", locatableBlock.position.y)
                        .append("z", locatableBlock.position.z))
                .append("Range", range)
        launch { database.insertOne(document) }
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
        launch { database.deleteOne(eq("Location", filter)) }
    }

    fun changeRange(locatableBlock: LocatableBlock, range: Short) {
        delChunkLoader(locatableBlock)
        addChunkLoader(locatableBlock, range)
    }
}
