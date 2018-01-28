package one.oktw.galaxy.internal

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.text
import com.mongodb.client.model.Projections
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.internal.enums.Group.ADMIN
import one.oktw.galaxy.internal.enums.Group.MEMBER
import one.oktw.galaxy.internal.types.Galaxy
import one.oktw.galaxy.internal.types.Member
import one.oktw.galaxy.internal.types.Planet
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

    fun getPlanet(worldUUID: UUID): Planet? {
        return galaxyCollection.find(Filters.eq("planets.world", worldUUID))
                .projection(Projections.slice("planets", 1)).first()?.planets?.get(0)
    }
}
