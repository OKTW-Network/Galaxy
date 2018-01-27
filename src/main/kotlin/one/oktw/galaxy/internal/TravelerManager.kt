package one.oktw.galaxy.internal

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections.slice
import com.mongodb.client.model.UpdateOptions
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.internal.types.Galaxy
import one.oktw.galaxy.internal.types.Position
import one.oktw.galaxy.internal.types.Traveler
import org.spongepowered.api.entity.living.player.Player

class TravelerManager {
    companion object {
        private val travelerCollation = databaseManager.database.getCollection("Traveler", Traveler::class.java)
        private val galaxyCollation = databaseManager.database.getCollection("Galaxy", Galaxy::class.java)

        private fun createTraveler(player: Player): Traveler {
            val traveler = Traveler(player.uniqueId, position = Position().fromPosition(player.location.position))
            traveler.save()
            return traveler
        }

        fun getTraveler(player: Player): Traveler {
            return travelerCollation.find(eq("uuid", player.uniqueId)).first() ?: createTraveler(player)
        }

        fun saveTraveler(traveler: Traveler) {
            travelerCollation.replaceOne(eq("uuid", traveler.uuid), traveler, UpdateOptions().upsert(true))
        }

        fun updateTraveler(player: Player) {
            val traveler = getTraveler(player)
            val galaxy = galaxyCollation
                    .find(eq("planets.world", player.world.uniqueId))
                    .projection(slice("planets", 1)).first()

            traveler.position.fromPosition(player.location.position)
            traveler.position.planet = galaxy.planets[0].uuid

            traveler.save()
        }
    }
}
