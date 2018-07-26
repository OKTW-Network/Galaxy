package one.oktw.galaxy.machine.portal

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.reactivestreams.client.FindPublisher
import kotlinx.coroutines.experimental.reactive.awaitFirst
import kotlinx.coroutines.experimental.reactive.awaitFirstOrNull
import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.data.Position
import one.oktw.galaxy.internal.DatabaseManager
import one.oktw.galaxy.machine.portal.data.Portal
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import kotlin.collections.ArrayList

class PortalHelper {
    companion object {
        private val collection = DatabaseManager.database.getCollection("Portal", Portal::class.java)


        private fun isFrame(position: Location<World>): Boolean {
            return position[DataBlockType.key].orElse(null) == CustomBlocks.PORTAL_FRAME
        }

        private fun getNeighborFrames(center: Location<World>): List<Location<World>> {
            val list = ArrayList<Location<World>>()

            for (position in Arrays.asList(
                center.getBlockRelative(Direction.EAST),
                center.getBlockRelative(Direction.WEST),
                center.getBlockRelative(Direction.NORTH),
                center.getBlockRelative(Direction.SOUTH)
            )) {
                if (isFrame(position)) {
                    list += position
                }
            }

            return list
        }

        suspend fun searchPortalFrame(center: Location<World>, maxCount: Int): HashMap<Triple<Int, Int, Int>, Location<World>>? {
            val list = HashMap<Triple<Int, Int, Int>, Location<World>>()
            var generation = ArrayList<Location<World>>()

            center.let {
                list[Triple(it.blockX, it.blockY, it.blockZ)] = it
            }

            generation.add(center)

            withContext(Main.serverThread) {
                while (list.size <= maxCount) {
                    val newGeneration = ArrayList<Location<World>>()
                    var found = false

                    for (i in generation) {
                        val newItems = getNeighborFrames(i).filter {
                            list[Triple(it.blockX, it.blockY, it.blockZ)] == null
                        }

                        if (newItems.isNotEmpty()) {
                            found = true

                            newItems.forEach {
                                list[Triple(it.blockX, it.blockY, it.blockZ)] = it
                                newGeneration += it
                            }
                        }
                    }

                    generation = newGeneration

                    if (!found) {
                        break
                    }
                }
            }

            if (list.size > maxCount) {
                return null
            }

            return list
        }


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
