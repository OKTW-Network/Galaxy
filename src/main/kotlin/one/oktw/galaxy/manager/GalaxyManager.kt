package one.oktw.galaxy.manager

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.text
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.enums.Group.OWNER
import one.oktw.galaxy.helper.PlanetHelper
import one.oktw.galaxy.types.Galaxy
import one.oktw.galaxy.types.Member
import one.oktw.galaxy.types.Planet
import one.oktw.galaxy.types.Traveler
import org.bson.conversions.Bson
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

class GalaxyManager {
    private val collection = databaseManager.database.getCollection("Galaxy", Galaxy::class.java)
    private val galaxyCache = ConcurrentHashMap<UUID, Galaxy>()

    fun createGalaxy(name: String, creator: Player, vararg members: UUID): Galaxy {
        val memberList = ArrayList<Member>(members.size + 1)
        memberList += Member(creator.uniqueId, OWNER)
        memberList += members.map { Member(it) }

        val galaxy = Galaxy(name = name, members = memberList)

        launch { collection.insertOne(galaxy) }
        return galaxy
    }

    fun saveGalaxy(galaxy: Galaxy) = launch {
        collection.replaceOne(eq("uuid", galaxy.uuid), galaxy)
    }

    suspend fun deleteGalaxy(uuid: UUID) {
        getGalaxy(uuid).await()?.planets?.forEach {
            it.world.let { PlanetHelper.removePlanet(it) }
        }

        galaxyCache -= uuid

        launch { collection.deleteOne(eq("uuid", uuid)) }
    }

    fun getGalaxy(uuid: UUID): Deferred<Galaxy?> = async {
        galaxyCache.getOrPut(uuid) { collection.find(eq("uuid", uuid)).first() }
    }

    fun getGalaxy(planet: Planet) = async {
        galaxyCache.values.firstOrNull { planet in it.planets } ?: collection.find(
            eq(
                "planets.uuid",
                planet.uuid
            )
        ).first()
    }

    fun listGalaxy() = async { collection.find().asSequence() }

    fun listGalaxy(filter: Bson) = async { collection.find(filter).asSequence() }

    fun listGalaxy(traveler: Traveler) = async { collection.find(eq("members.uuid", traveler.uuid)).asSequence() }

    fun searchGalaxy(keyword: String) = async { collection.find(text(keyword)).asSequence() }

    fun getPlanet(uuid: UUID) = async {
        collection.distinct("planets", eq("planets.uuid", uuid), Planet::class.java).firstOrNull()
    }

    fun getPlanetFromWorld(uuid: UUID) = async {
        collection.distinct("planets", eq("planets.world", uuid), Planet::class.java).firstOrNull()
    }
}
