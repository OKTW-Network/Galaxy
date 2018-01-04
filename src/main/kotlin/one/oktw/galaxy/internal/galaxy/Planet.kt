package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.internal.galaxy.PlanetManager.Companion.loadPlanet
import org.bson.Document
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World
import org.spongepowered.api.world.storage.WorldProperties
import java.util.*

class Planet internal constructor(val uuid: UUID) {
    private val server = Sponge.getServer()
    private val planetCollection = databaseManager.database.getCollection("Planet")
    private val planet: Document

    init {
        this.planet = planetCollection.find(eq("UUID", uuid)).first()
    }

    var name: String
        get() = planet.getString("Name")
        set(name) {
            launch { planetCollection.findOneAndUpdate(eq("UUID", uuid), set("name", name)) }
        }

    var size: Int
        get() = planet.getInteger("Size")
        set(size) {
            val properties = worldProp.get()

            properties.worldBorderTargetDiameter = (size * 16).toDouble()
            if (server.saveWorldProperties(properties)) {
                launch { planetCollection.findOneAndUpdate(eq("UUID", uuid), set("Size", size)) }
            }
        }

    var security: SecurityLevel
        get() = SecurityLevel.fromInt(planet.getInteger("Security"))
        set(securityLevel) {
            launch { planetCollection.findOneAndUpdate(eq("UUID", uuid), set("Security", securityLevel.level)) }
        }

    val world: Optional<World>
        get() = loadPlanet(uuid)

    val worldProp: Optional<WorldProperties>
        get() = server.getWorldProperties(uuid)
}
