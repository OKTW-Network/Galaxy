package one.oktw.galaxy.machine.portal

import com.mongodb.client.model.Filters.and
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.internal.DatabaseManager
import one.oktw.galaxy.machine.portal.data.Portal
import com.mongodb.client.model.Filters.eq
import com.mongodb.reactivestreams.client.FindPublisher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.awaitFirst
import kotlinx.coroutines.experimental.reactive.awaitFirstOrNull
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.galaxy.planet.data.Position
import java.util.*

class PortalHelper {
    companion object {
        private val collection = DatabaseManager.database.getCollection("Portal", Portal::class.java)

        suspend fun getPortal(uuid: UUID): Portal? {
            return collection.find(
                and(
                    eq("uuid", uuid)
                )
            ).awaitFirstOrNull()
        }

        suspend fun getPortalAt(planet: Planet, x: Int, y: Int, z: Int): Portal? {
            return collection.find(
                and(
                    eq("position.planet", planet.uuid),
                    eq("position.x", x),
                    eq("position.y", y),
                    eq("position.z", z)
                )
            ).awaitFirstOrNull()
        }

        suspend fun getAvailableTargets(uuid: UUID): FindPublisher<Portal>? {
            return getPortal(uuid)?.let { getAvailableTargets(it) } ?: return null
        }

        suspend fun getAvailableTargets(planet: Planet, x: Int, y: Int, z: Int): FindPublisher<Portal>? {
            return getPortalAt(planet, x, y, z)?.let { getAvailableTargets(it) } ?: return null
        }

        fun getAvailableTargets(origin: Portal): FindPublisher<Portal> {
            if (origin.crossPlanet) {
                return collection.find(
                    eq("galaxy", origin.galaxy)
                )
            } else {
                return collection.find(
                    eq("position.planet", origin.position.planet)
                )
            }
        }

        suspend fun createPortal(planet: Planet, x: Int, y: Int, z: Int, name: String, crossPlanet: Boolean): Boolean {
            val galaxy = galaxyManager.get(null, planet.uuid) ?: return false

            val portal = Portal(
                UUID.randomUUID(),
                name,
                galaxy.uuid,
                Position(x.toDouble(), y.toDouble(), z.toDouble(), planet.uuid),
                crossPlanet
            )

            return collection.insertOne(portal).awaitFirstOrNull() != null
        }

        suspend fun removePortal(planet: Planet, x: Int, y: Int, z: Int): Boolean {
            val portal = getPortalAt(planet, x, y, z) ?: return false

            return collection.deleteMany(eq("uuid", portal.uuid)).awaitFirst().deletedCount > 0
        }
    }
}