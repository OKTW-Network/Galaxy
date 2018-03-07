package one.oktw.galaxy.manager

import com.mongodb.client.model.Filters.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.enums.Group.*
import one.oktw.galaxy.helper.PlanetHelper
import one.oktw.galaxy.types.Galaxy
import one.oktw.galaxy.types.Member
import one.oktw.galaxy.types.Planet
import one.oktw.galaxy.types.Traveler
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import kotlin.collections.ArrayList

class GalaxyManager {
    private val galaxyCollection = databaseManager.database.getCollection("Galaxy", Galaxy::class.java)

    fun createGalaxy(name: String, creator: Player, vararg members: UUID): Galaxy {
        val memberList = ArrayList<Member>(members.size + 1)
        memberList += Member(creator.uniqueId, OWNER)
        memberList += members.map { Member(it) }

        val galaxy = Galaxy(name = name, members = memberList)

        launch { galaxyCollection.insertOne(galaxy) }
        return galaxy
    }

    fun saveGalaxy(galaxy: Galaxy) = launch { galaxyCollection.replaceOne(eq("uuid", galaxy.uuid), galaxy) }

    suspend fun deleteGalaxy(uuid: UUID) {
        getGalaxy(uuid).await().ifPresent {
            it.planets.forEach {
                it.world.let { PlanetHelper.removePlanet(it) }
            }
        }

        launch { galaxyCollection.deleteOne(eq("uuid", uuid)) }
    }

    fun getGalaxy(uuid: UUID) = async { Optional.ofNullable(galaxyCollection.find(eq("uuid", uuid)).first()) }

    fun getGalaxy(planet: Planet) = async { galaxyCollection.find(eq("planets.uuid", planet.uuid)).first() }

    fun listGalaxy() = async { galaxyCollection.find().iterator() }

    fun listGalaxyForTraveler(traveler: Traveler) = async {
        galaxyCollection.find(and(eq("members.uuid", traveler.uuid), `in`("members.group", MEMBER, ADMIN))).iterator()
    }

    fun searchGalaxy(keyword: String) = async {
        val galaxyList = ArrayList<Galaxy>()
        galaxyCollection.find(text(keyword)).forEach { galaxyList += it }
        galaxyList
    }

    fun getPlanet(uuid: UUID) = async {
        galaxyCollection.distinct("planets", eq("planets.uuid", uuid), Planet::class.java).firstOrNull()
    }

    fun getPlanetFromWorld(uuid: UUID) = async {
        galaxyCollection.distinct("planets", eq("planets.world", uuid), Planet::class.java).firstOrNull()
    }
}
