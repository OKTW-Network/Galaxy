package one.oktw.galaxy.internal

import com.flowpowered.math.vector.Vector3i
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.Main.Companion.main
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.LocatableBlock

class ChunkLoaderManager {
    private val ticketManager = Sponge.getServer().chunkTicketManager
    private val database = databaseManager.database.getCollection("ChunkLoader")

    init {
        ticketManager.registerCallback(main) { tickets, world ->
            main.logger.info("registerCallback")
            //TODO reForce loading chunks
        }
    }

    fun addChunkLoader(block: LocatableBlock, range: Short): HashSet<Vector3i> {
        val blockPos = block.position
        val document = Document("World", block.world.uniqueId)
                .append("Location", Document("x", blockPos.x).append("y", blockPos.y).append("z", blockPos.z))
                .append("Range", range)
        database.insertOne(document)
        val ticket = ticketManager.createTicket(main, block.world).get()
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
        return chunkList
    }

    fun delChunkLoader(block: LocatableBlock) {
        val blockPos = block.position
    }
}
