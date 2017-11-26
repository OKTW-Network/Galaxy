package one.oktw.galaxy.internal

import com.flowpowered.math.vector.Vector3i
import com.mongodb.client.model.Filters.eq
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
        ticketManager.registerCallback(main) { tickets, world ->
            main.logger.info("registerCallback")
        }
    }

    fun addChunkLoader(block: LocatableBlock, range: Short) {
        val blockPos = block.position
        val document = Document("World", block.world.uniqueId)
                .append("Location", Document("x", blockPos.x).append("y", blockPos.y).append("z", blockPos.z))
                .append("Range", range)
        database.insertOne(document)
        val ticket = ticketManager.createTicket(main, block.world).ifPresent { ticket ->
            val chunkPos = block.location.chunkPosition
            val chunkList = HashSet<Vector3i>()
            for (x in chunkPos.x - range..chunkPos.x + range) {
                for (z in chunkPos.z - range..chunkPos.z + range) {
                    main.logger.info("loading chunk: ({},{})", x, z)
                    chunkList.add(Vector3i(x, 0, z))
                }
            }
            if (chunkList.size > ticket.numChunks) {
                ticket.numChunks = chunkList.size
            }
            chunkList.forEach(ticket::forceChunk)
        }
    }

    fun delChunkLoader(block: LocatableBlock) {
        val blockPos = block.position
        val filter = Document("x", blockPos.x).append("y", blockPos.y).append("z", blockPos.z)
        database.deleteOne(eq("Location", filter))
        reloadChunkLoader(block.world)
    }

    private fun reloadChunkLoader(world: World) {// TODO need cleanup
        ticketManager.getForcedChunks(world).forEach { _, ticket ->
            ticket.release()
        }
        database.find(eq("World", world.uniqueId)).forEach { document: Document ->
            val location = document["Location"] as Document
            ticketManager.createTicket(main, world).ifPresent { ticket ->
                val chunkPos = world.getLocation(location["x"] as Int, location["y"] as Int, location["z"] as Int).chunkPosition
                val chunkList = HashSet<Vector3i>()
                val range = document["Range"] as Int
                for (x in chunkPos.x - range..chunkPos.x + range) {
                    for (z in chunkPos.z - range..chunkPos.z + range) {
                        main.logger.info("loading chunk: ({},{})", x, z)
                        chunkList.add(Vector3i(x, 0, z))
                    }
                }
                if (chunkList.size > ticket.numChunks) {
                    ticket.numChunks = chunkList.size
                }
                chunkList.forEach(ticket::forceChunk)
            }
        }
    }
}
