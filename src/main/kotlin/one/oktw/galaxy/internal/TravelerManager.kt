package one.oktw.galaxy.internal

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.internal.types.Position
import one.oktw.galaxy.internal.types.Traveler
import org.spongepowered.api.entity.living.player.Player

class TravelerManager {
    companion object {
        private val collation = databaseManager.database.getCollection("Traveler", Traveler::class.java)

        private fun createTraveler(player: Player): Traveler {
            val traveler = Traveler(player.uniqueId, position = Position().fromPosition(player.location.position))
            traveler.save()
            return traveler
        }

        fun getTraveler(player: Player): Traveler {
            return collation.find(eq("uuid", player.uniqueId)).first() ?: createTraveler(player)
        }

        fun saveTraveler(traveler: Traveler) {
            collation.replaceOne(eq("uuid", traveler.uuid), traveler, UpdateOptions().upsert(true))
        }
    }
}
