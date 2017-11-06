package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.planetManager
import org.bson.Document
import org.spongepowered.api.world.World
import java.util.*
import java.util.stream.Collectors.toList

class Galaxy internal constructor(uuid: UUID) {
    private val database = Main.databaseManager.database
    private val galaxies = database.getCollection("Galaxy")
    private val galaxy: Document

    val uniqueId: UUID
        get() = galaxy["UUID"] as UUID

    val name: String
        get() = galaxy.getString("Name")

    val members: List<UUID>
        get() = (galaxy["Members"] as List<Document>).parallelStream().map { document -> document["UUID"] as UUID }.collect(toList())

    val worlds: List<UUID>
        get() = galaxy["Worlds"] as List<UUID>

    init {
        galaxy = galaxies.find(eq("UUID", uuid)).first()
    }

    fun createWorld(name: String): Optional<World> {
        val worlds = database.getCollection("World")
        return if (worlds.find(eq("Name", name)).first() == null) {
            val planet = planetManager.createWorld(name)
            galaxies.findOneAndUpdate(
                    eq("UUID", uniqueId),
                    Document("\$push", Document("Worlds", planet.uniqueId))
            )
            planet.world
        } else {
            Optional.empty()
        }
    }
}
