package one.oktw.galaxy.manager

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.text
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.enums.Group.ADMIN
import one.oktw.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.helper.PlanetHelper
import one.oktw.galaxy.types.Galaxy
import one.oktw.galaxy.types.Member
import one.oktw.galaxy.types.Planet
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import java.util.stream.Collectors.toList
import kotlin.collections.ArrayList

class GalaxyManager {
    private val galaxyCollection = databaseManager.database.getCollection("Galaxy", Galaxy::class.java)

    fun createGalaxy(name: String, creator: Player, vararg members: UUID): Galaxy {
        val memberList = listOf(*members).parallelStream()
                .map { member -> Member(member, MEMBER) }
                .collect(toList())
        memberList += Member(creator.uniqueId, ADMIN)

        val galaxy = Galaxy(name = name, members = memberList.filterNotNull())

        launch { galaxyCollection.insertOne(galaxy) }
        return galaxy
    }

    fun saveGalaxy(galaxy: Galaxy) {
        launch { galaxyCollection.replaceOne(eq("uuid", galaxy.uuid), galaxy) }
    }

    fun deleteGalaxy(uuid: UUID) {
        getGalaxy(uuid).ifPresent {
            it.planets.forEach {
                PlanetHelper.removePlanet(it.world!!)
            }
        }

        launch { galaxyCollection.deleteOne(eq("uuid", uuid)) }
    }

    fun getGalaxy(uuid: UUID): Optional<Galaxy> {
        return Optional.ofNullable(galaxyCollection.find(eq("uuid", uuid)).first())
    }

    fun getGalaxy(planet: Planet): Galaxy {
        return galaxyCollection.find(eq("planets.uuid", planet.uuid)).first()!!
    }

    fun searchGalaxy(keyword: String): ArrayList<Galaxy> {
        val galaxyList = ArrayList<Galaxy>()
        galaxyCollection.find(text(keyword)).forEach { galaxyList += it }
        return galaxyList
    }

    fun getPlanet(uuid: UUID): Planet? {
        return galaxyCollection.distinct("planets", eq("planets.uuid", uuid), Planet::class.java).firstOrNull()
    }

    fun getPlanetFromWorld(uuid: UUID): Planet? {
        return galaxyCollection.distinct("planets", eq("planets.world", uuid), Planet::class.java).firstOrNull()
    }
}
