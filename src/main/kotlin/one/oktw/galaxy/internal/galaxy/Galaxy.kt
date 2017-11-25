package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.*
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

    init {
        galaxy = galaxies.find(eq("UUID", uuid)).first()
    }

    val uniqueId: UUID
        get() = galaxy["UUID"] as UUID

    var name: String
        get() = galaxy.getString("Name")
        set(name) {
            galaxies.findOneAndUpdate(eq("UUID", uniqueId), set("Name", name))
        }

    val members: List<UUID>
        get() = (galaxy["Members"] as List<Document>).parallelStream().map { document -> document["UUID"] as UUID }.collect(toList())

    val worlds: List<UUID>
        get() = galaxy["Worlds"] as List<UUID>

    fun addMember(member: UUID): Boolean {
        if (!members.any { it == member }) {
            galaxies.findOneAndUpdate(eq("UUID", uniqueId),
                    push("Members", member)
            )
            return true
        }
        return false
    }

    fun deleteMember(member: UUID): Boolean {
        if (members.any { it == member }) {
            galaxies.findOneAndUpdate(eq("UUID", uniqueId),
                    pull("Members", member)
            )
            return true
        }
        return false
    }

    fun createWorld(name: String): Optional<World> {
        val worlds = database.getCollection("World")
        return if (worlds.find(eq("Name", name)).first() == null) {
            val planet = planetManager.createWorld(name)
            galaxies.findOneAndUpdate(eq("UUID", uniqueId),
                    push("Worlds", planet.uniqueId)
            )
            planet.world
        } else {
            Optional.empty()
        }
    }

    fun deleteWorld(world: UUID) {
        galaxies.findOneAndUpdate(eq("UUID", uniqueId),
                pull("Worlds", world)
        )
    }
}
