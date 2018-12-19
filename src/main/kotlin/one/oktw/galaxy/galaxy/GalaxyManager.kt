package one.oktw.galaxy.galaxy

import com.mongodb.client.model.Filters.eq
import com.mongodb.reactivestreams.client.FindPublisher
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import one.oktw.galaxy.Main.Companion.main
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

class GalaxyManager private constructor() {
    companion object {
        private val instance = GalaxyManager()

        fun getInstance() = instance
    }

    private val collection = database.getCollection("Galaxy", Galaxy::class.java)
    private val saveLock = Mutex()

    suspend fun createGalaxy(name: String, creator: Player, vararg members: UUID): Galaxy {
        val memberList = ArrayList<Traveler>(members.size + 1)
        memberList += Traveler(creator.uniqueId, OWNER)
        memberList += members.map { Traveler(it) }

        val galaxy = Galaxy(name = name, members = memberList)

        collection.insertOne(galaxy).awaitFirstOrNull()
        return galaxy
    }

    suspend fun saveGalaxy(galaxy: Galaxy) {
        try {
            saveLock.withLock { collection.findOneAndReplace(eq("uuid", galaxy.uuid), galaxy).awaitFirstOrNull() }
        } catch (exception: Exception) {
            main.logger.error("Failed save galaxy: {}({})", galaxy.uuid, galaxy.name)
            throw exception
        }
    }

    suspend fun deleteGalaxy(uuid: UUID) {
        get(uuid)?.planets?.forEach { PlanetHelper.removePlanet(it.world) }

        collection.deleteOne(eq("uuid", uuid)).awaitFirstOrNull()
    }

    suspend fun get(uuid: UUID? = null, planet: UUID? = null): Galaxy? {
        return uuid?.let { collection.find(eq("uuid", uuid)).first().awaitFirstOrNull() }
            ?: planet?.let { collection.find(eq("planets.uuid", planet)).first().awaitFirstOrNull() }
    }


    suspend fun get(worldProperties: WorldProperties): Galaxy? {
        return collection.find(eq("planets.world", worldProperties.uniqueId)).first().awaitFirstOrNull()
    }

    suspend fun get(world: World) = get(world.properties)

    fun get(player: Player): FindPublisher<Galaxy> = collection.find(eq("members.uuid", player.uniqueId))

    fun listGalaxy(): FindPublisher<Galaxy> = collection.find()

    fun listGalaxy(filter: Bson): FindPublisher<Galaxy> = collection.find(filter)
}
