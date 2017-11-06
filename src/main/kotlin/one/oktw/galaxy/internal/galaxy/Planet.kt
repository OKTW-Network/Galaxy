package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.planetManager
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World
import org.spongepowered.api.world.storage.WorldProperties
import java.util.*

class Planet internal constructor(uuid: UUID) {
    private val server = Sponge.getServer()
    private val database = Main.databaseManager.database
    private val planet: Document

    val uniqueId: UUID
        get() = planet["UUID"] as UUID

    val name: String
        get() = planet.getString("Name")

    val size: Int
        get() = planet.getInteger("Size")

    val world: Optional<World>
        get() = planetManager.loadWorld(uniqueId)

    val worldProp: Optional<WorldProperties>
        get() = server.getWorldProperties(uniqueId)

    init {
        this.planet = database.getCollection("World").find(eq("UUID", uuid)).first()
    }

    fun setSize(size: Int): Int {
        val properties = worldProp.get()

        database.getCollection("World").findOneAndUpdate(
                eq("UUID", uniqueId),
                Document("\$set", Document("Size", size))
        )

        properties.worldBorderTargetDiameter = (size * 16).toDouble()
        server.saveWorldProperties(properties)

        return size - size
    }

    fun setSecurity(level: Int) {
        database.getCollection("world").findOneAndUpdate(
                eq("UUID", uniqueId),
                Document("\$set", Document("Security", level))
        )
    }
}
