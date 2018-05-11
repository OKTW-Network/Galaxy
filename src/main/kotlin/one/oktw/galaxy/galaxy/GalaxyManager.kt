package one.oktw.galaxy.galaxy

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.text
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.enums.Group.OWNER
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.Member
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.internal.DatabaseManager.Companion.database
import one.oktw.galaxy.traveler.data.Traveler
import org.bson.conversions.Bson
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class GalaxyManager {
    private val collection = database.getCollection("Galaxy", Galaxy::class.java)
    private val cache = ConcurrentHashMap<UUID, Galaxy>()

    init {
        Task.builder()
            .name("GalaxyManager")
            .async()
            .interval(5, TimeUnit.MINUTES)
            .execute(::saveAll)
            .submit(main)
    }

    fun createGalaxy(name: String, creator: Player, vararg members: UUID): Galaxy {
        val memberList = ArrayList<Member>(members.size + 1)
        memberList += Member(creator.uniqueId, OWNER)
        memberList += members.map { Member(it) }

        val galaxy = Galaxy(name = name, members = memberList)

        launch { collection.insertOne(galaxy) }
        return galaxy
    }

    fun saveGalaxy(galaxy: Galaxy) {
        collection.replaceOne(eq("uuid", galaxy.uuid), galaxy)
        cache -= galaxy.uuid
    }

    suspend fun deleteGalaxy(uuid: UUID) {
        getGalaxy(uuid).await()?.planets?.forEach {
            it.world.let { PlanetHelper.removePlanet(it!!) }
        }

        cache -= uuid

        launch { collection.deleteOne(eq("uuid", uuid)) }
    }

    fun getGalaxy(uuid: UUID): Deferred<Galaxy?> = async {
        cache.getOrPut(uuid) { collection.find(eq("uuid", uuid)).first() }
    }

    fun getGalaxy(planet: Planet) = async {
        cache.values.firstOrNull { planet in it.planets } ?: collection.find(eq("planets.uuid", planet.uuid)).first()
    }

    // unsafe write
    fun listGalaxy() = async { collection.find().asSequence() }

    fun listGalaxy(filter: Bson) = async { collection.find(filter).asSequence() }

    fun listGalaxy(traveler: Traveler) = async { collection.find(eq("members.uuid", traveler.uuid!!)).asSequence() }

    fun searchGalaxy(keyword: String) = async { collection.find(text(keyword)).asSequence() }

    fun getPlanet(uuid: UUID) = async {
        collection.distinct("planets", eq("planets.uuid", uuid), Planet::class.java).firstOrNull { it.uuid == uuid }
    }

    fun getPlanetFromWorld(uuid: UUID) = async {
        collection.distinct("planets", eq("planets.world", uuid), Planet::class.java).firstOrNull { it.world == uuid }
    }

    fun saveAll() {
        cache.forEachValue(10, ::saveGalaxy)
    }
}
