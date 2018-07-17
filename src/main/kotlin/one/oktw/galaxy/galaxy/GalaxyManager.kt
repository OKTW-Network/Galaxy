package one.oktw.galaxy.galaxy

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.awaitFirstOrNull
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import one.oktw.galaxy.internal.DatabaseManager.Companion.database
import org.bson.conversions.Bson
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.World
import org.spongepowered.api.world.storage.WorldProperties
import java.util.*
import kotlin.collections.ArrayList

class GalaxyManager {
    private val collection = database.getCollection("Galaxy", Galaxy::class.java)

    fun createGalaxy(name: String, creator: Player, vararg members: UUID): Galaxy {
        val memberList = ArrayList<Traveler>(members.size + 1)
        memberList += Traveler(creator.uniqueId, OWNER)
        memberList += members.map { Traveler(it) }

        val galaxy = Galaxy(name = name, members = memberList)

        launch { collection.insertOne(galaxy) }
        return galaxy
    }

    fun saveGalaxy(galaxy: Galaxy) {
        collection.findOneAndReplace(eq("uuid", galaxy.uuid), galaxy)
    }

    suspend fun deleteGalaxy(uuid: UUID) {
        get(uuid).await()?.planets?.forEach {
            it.world.let { PlanetHelper.removePlanet(it) }
        }

        launch { collection.deleteOne(eq("uuid", uuid)) }
    }

    fun get(uuid: UUID? = null, planet: UUID? = null) = async {
        uuid?.let { collection.find(eq("uuid", uuid)).first().awaitFirstOrNull() }
                ?: planet?.let { collection.find(eq("planets.uuid", planet)).first().awaitFirstOrNull() }
    }

    fun get(worldProperties: WorldProperties): Deferred<Galaxy?> = async {
        collection.find(eq("planets.world", worldProperties.uniqueId)).first().awaitFirstOrNull()
    }

    fun get(world: World) = get(world.properties)

    fun get(player: Player) = async { collection.find(eq("members.uuid", player.uniqueId)).openSubscription() }

    fun listGalaxy() = async { collection.find().openSubscription() }

    fun listGalaxy(filter: Bson) = async { collection.find(filter).openSubscription() }
}
