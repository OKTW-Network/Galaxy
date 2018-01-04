package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.*
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.internal.galaxy.PlanetManager.Companion.removePlanet
import org.bson.Document
import org.spongepowered.api.world.World
import java.util.*
import java.util.stream.Collectors.toList

class Galaxy constructor(val uuid: UUID) {
    private val database = Main.databaseManager.database
    private val galaxyCollection = database.getCollection("Galaxy")
    private val galaxy = galaxyCollection.find(eq("UUID", uuid)).first()

    var name: String
        get() = galaxy.getString("Name")
        set(name) {
            launch { galaxyCollection.findOneAndUpdate(eq("UUID", uuid), set("Name", name)) }
        }

    val members: List<UUID>
        get() = (galaxy["Members"] as List<Document>).parallelStream().map { document -> document["UUID"] as UUID }.collect(toList())

    val planets: List<UUID>
        get() = galaxy["Planets"] as List<UUID>

    fun addMember(member: UUID): Boolean {
        if (!members.any { it == member }) {
            launch {
                galaxyCollection.findOneAndUpdate(eq("UUID", uuid),
                        push("Members", member)
                )
            }
            return true
        }
        return false
    }

    fun deleteMember(member: UUID): Boolean {
        if (members.any { it == member }) {
            launch {
                galaxyCollection.findOneAndUpdate(eq("UUID", uuid),
                        pull("Members", member)
                )
            }
            return true
        }
        return false
    }

    fun createPlanet(name: String): Optional<World> {
        return if (database.getCollection("Planet").find(eq("Name", name)).first() == null) {
            val planet = PlanetManager.createPlanet(name)
            galaxyCollection.findOneAndUpdate(eq("UUID", uuid),
                    push("Planets", planet.uuid)
            )
            planet.world
        } else {
            Optional.empty()
        }
    }

    fun deletePlanet(world: UUID) {
        galaxyCollection.findOneAndUpdate(eq("UUID", uuid),
                pull("Planets", world)
        )
        removePlanet(world)
    }
}
