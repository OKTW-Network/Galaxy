package one.oktw.galaxy.traveler

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.galaxy.planet.data.Position
import one.oktw.galaxy.galaxy.planet.data.extensions.fromVector3d
import one.oktw.galaxy.internal.DatabaseManager.Companion.database
import one.oktw.galaxy.traveler.data.Traveler
import one.oktw.galaxy.traveler.data.extensions.save
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class TravelerManager {
    private val collection = database.getCollection("Traveler", Traveler::class.java)
    private val cache = ConcurrentHashMap<UUID, Traveler>()

    init {
        Task.builder()
            .name("TravelerManager")
            .async()
            .interval(5, TimeUnit.MINUTES)
            .execute(::saveAll)
            .submit(main)
    }

    private fun createTraveler(player: Player): Traveler {
        val traveler = Traveler(player.uniqueId, position = Position(player.location.position))
        traveler.save()
        return traveler
    }

    fun getTraveler(player: Player): Traveler {
        return cache.getOrPut(player.uniqueId) {
            collection.find(eq("uuid", player.uniqueId)).first() ?: createTraveler(player)
        }
    }

    fun getTraveler(uuid: UUID): Traveler? {
        return cache[uuid] ?: collection.find(eq("uuid", uuid)).first()?.also { cache[uuid] = it }
    }

    fun saveTraveler(traveler: Traveler) {
        launch { collection.replaceOne(eq("uuid", traveler.uuid!!), traveler, ReplaceOptions().upsert(true)) }
        cache.remove(traveler.uuid)
    }

    suspend fun updateTraveler(player: Player) {
        val traveler = getTraveler(player)

        traveler.position.fromVector3d(player.location.position)
        traveler.position.planet = galaxyManager.getPlanetFromWorld(player.world.uniqueId).await()?.uuid

        traveler.save()
    }

    fun saveAll() {
        cache.forEachValue(10, ::saveTraveler)
    }
}
