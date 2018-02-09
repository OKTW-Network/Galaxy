package one.oktw.galaxy.manager

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.types.Position
import one.oktw.galaxy.types.Traveler
import org.spongepowered.api.entity.living.player.Player
import java.util.*

class TravelerManager {
    private val travelerCollation = databaseManager.database.getCollection("Traveler", Traveler::class.java)
    private val cache = HashMap<UUID, Traveler>()

    private fun createTraveler(player: Player): Traveler {
        val traveler = Traveler(player.uniqueId, position = Position().fromPosition(player.location.position))
        traveler.save()
        return traveler
    }

    fun getTraveler(player: Player): Traveler {
        return cache.getOrPut(player.uniqueId) {
            travelerCollation.find(eq("uuid", player.uniqueId)).first() ?: createTraveler(player)
        }
    }

    fun saveTraveler(traveler: Traveler) {
        launch { travelerCollation.replaceOne(eq("uuid", traveler.uuid), traveler, UpdateOptions().upsert(true)) }
        cache.remove(traveler.uuid)
    }

    fun updateTraveler(player: Player) {
        val traveler = getTraveler(player)

        traveler.position.fromPosition(player.location.position)
        traveler.position.planet = galaxyManager.getPlanetFromWorld(player.world.uniqueId)?.uuid

        traveler.save()
    }
}
