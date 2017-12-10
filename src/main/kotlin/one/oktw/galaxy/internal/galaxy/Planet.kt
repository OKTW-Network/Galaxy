package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.internal.galaxy.PlanetManager.Companion.loadPlanet
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World
import org.spongepowered.api.world.storage.WorldProperties
import java.util.*

class Planet internal constructor(uuid: UUID) {
    private val server = Sponge.getServer()
    private val database = Main.databaseManager.database
    private val planetCollection = database.getCollection("Planet")
    private val planet: Document

    init {
        this.planet = planetCollection.find(eq("UUID", uuid)).first()
    }

    val uniqueId: UUID
        get() = planet["UUID"] as UUID

    var name: String
        get() = planet.getString("Name")
        set(name) {
            launch { planetCollection.findOneAndUpdate(eq("UUID", uniqueId), set("name", name)) }
        }

    var size: Int
        get() = planet.getInteger("Size")
        set(size) {
            val properties = worldProp.get()

            properties.worldBorderTargetDiameter = (size * 16).toDouble()
            if (server.saveWorldProperties(properties)) {
                launch { planetCollection.findOneAndUpdate(eq("UUID", uniqueId), set("Size", size)) }
            }
        }

    var security: SecurityLevel
        get() = SecurityLevel.fromInt(planet.getInteger("Security"))
        set(securityLevel) {
            launch { planetCollection.findOneAndUpdate(eq("UUID", uniqueId), set("Security", securityLevel.level)) }
        }

    val world: Optional<World>
        get() = loadPlanet(uniqueId)

    val worldProp: Optional<WorldProperties>
        get() = server.getWorldProperties(uniqueId)
}
