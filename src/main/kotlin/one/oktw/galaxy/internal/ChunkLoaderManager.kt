package one.oktw.galaxy.internal

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.Main.Companion.main
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.World
import java.util.*

class ChunkLoaderManager {
    private val ticketManager = Sponge.getServer().chunkTicketManager
    private val database = databaseManager.database.getCollection("ChunkLoader")

    init {
        ticketManager.registerCallback(main) { _, world ->
            launch { reloadChunkLoader(world) }
        }
    }

    fun addChunkLoader(block: LocatableBlock, range: Short) {
        val blockPos = block.position
        val document = Document("World", block.world.uniqueId)
                .append("Location", Document("x", blockPos.x).append("y", blockPos.y).append("z", blockPos.z))
                .append("Range", range)
        launch { database.insertOne(document) }
        val ticket = ticketManager.createTicket(main, block.world).get()
        val chunkPos = block.location.chunkPosition
        val chunkList = HashSet<Vector3i>()
        (chunkPos.x - range..chunkPos.x + range).forEach { x ->
            (chunkPos.z - range..chunkPos.z + range).forEach { z ->
                chunkList.add(Vector3i(x, 0, z))
            }
        }
        if (chunkList.size > ticket.numChunks) {
            main.logger.warn("ChunkLoader range({} chunks) large then forge limit({} chunks)!", chunkList.size, ticket.numChunks)
        }
        chunkList.parallelStream().forEach(ticket::forceChunk)
    }

    fun delChunkLoader(block: LocatableBlock) {
        val blockPos = block.position
        val filter = Document("x", blockPos.x).append("y", blockPos.y).append("z", blockPos.z)
        launch { database.deleteOne(eq("Location", filter)) }
        launch { reloadChunkLoader(block.world) }
    }

    private suspend fun reloadChunkLoader(world: World) {
        ticketManager.getForcedChunks(world).values().parallelStream().forEach { it.release() }
        database.find(eq("World", world.uniqueId)).forEach { document ->
            val location = document["Location"] as Document
            ticketManager.createTicket(main, world).ifPresent { ticket ->
                val chunkPos = world.getLocation(location["x"] as Int, location["y"] as Int, location["z"] as Int).chunkPosition
                val chunkList = HashSet<Vector3i>()
                val range = document["Range"] as Int
                (chunkPos.x - range..chunkPos.x + range).forEach { x ->
                    (chunkPos.z - range..chunkPos.z + range).forEach { z ->
                        chunkList.add(Vector3i(x, 0, z))
                    }
                }
                if (chunkList.size > ticket.numChunks) {
                    main.logger.warn("ChunkLoader range({} chunks) large then forge limit({} chunks)!", chunkList.size, ticket.numChunks)
                }
                chunkList.parallelStream().forEach(ticket::forceChunk)
            }
        }
    }
}
