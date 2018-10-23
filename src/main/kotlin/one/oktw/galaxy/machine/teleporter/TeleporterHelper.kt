package one.oktw.galaxy.machine.teleporter

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
import one.oktw.galaxy.machine.teleporter.data.Teleporter
import one.oktw.galaxy.util.CountDown
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.property.block.MatterProperty
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TeleporterHelper {
    companion object {
        private val collection = DatabaseManager.database.getCollection("Teleporter", Teleporter::class.java)


        private fun isFrame(position: Location<World>): Boolean {
            return position[DataBlockType.key].orElse(null) == CustomBlocks.TELEPORTER_FRAME
        }

        private fun getNeighborFrames(center: Location<World>): List<Location<World>> {
            val list = ArrayList<Location<World>>()

            for (position in Arrays.asList(
                center.getBlockRelative(Direction.EAST),
                center.getBlockRelative(Direction.WEST),
                center.getBlockRelative(Direction.NORTH),
                center.getBlockRelative(Direction.SOUTH),
                center.getBlockRelative(Direction.UP),
                center.getBlockRelative(Direction.DOWN)
            )) {
                if (isFrame(position)) {
                    list += position
                }
            }

            return list
        }

        suspend fun searchTeleporterFrame(center: Location<World>, maxCount: Int): HashMap<Triple<Int, Int, Int>, Location<World>>? {
            val list = HashMap<Triple<Int, Int, Int>, Location<World>>()
            var generation = ArrayList<Location<World>>()

            center.let {
                list[Triple(it.blockX, it.blockY, it.blockZ)] = it
            }

            list[center.run { Triple(blockX, blockY, blockZ) }] = center
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

        fun filterSafeLocation(map: HashMap<Triple<Int, Int, Int>, Location<World>>): HashMap<Triple<Int, Int, Int>, Location<World>> {
            val newMap: HashMap<Triple<Int, Int, Int>, Location<World>> = HashMap()

            map.entries.forEach { (key, loc) ->
                if (
                    (loc.add(0.0, 1.0, 0.0).block.getProperty(MatterProperty::class.java).orElse(null)?.value !=
                        MatterProperty.Matter.SOLID) &&
                    (loc.add(0.0, 2.0, 0.0).block.getProperty(MatterProperty::class.java).orElse(null)?.value !=
                        MatterProperty.Matter.SOLID)
                ) {
                    newMap[key] = loc
                }
            }

            return newMap
        }

        suspend fun get(uuid: UUID): Teleporter? {
            return collection.find(
                and(
                    eq("uuid", uuid)
                )
            ).awaitFirstOrNull()
        }

        suspend fun get(planet: Planet, x: Int, y: Int, z: Int): Teleporter? {
            return collection.find(
                and(
                    eq("position.planet", planet.uuid),
                    eq("position.x", x),
                    eq("position.y", y),
                    eq("position.z", z)
                )
            ).awaitFirstOrNull()
        }

        suspend fun getAvailableTargets(uuid: UUID): FindPublisher<Teleporter>? {
            return get(uuid)?.let { getAvailableTargets(it) } ?: return null
        }

        suspend fun getAvailableTargets(planet: Planet, x: Int, y: Int, z: Int): FindPublisher<Teleporter>? {
            return get(planet, x, y, z)?.let { getAvailableTargets(it) } ?: return null
        }

        fun getAvailableTargets(origin: Teleporter): FindPublisher<Teleporter> {
            return if (origin.crossPlanet) {
                collection.find(
                    eq("galaxy", origin.galaxy)
                )
            } else {
                collection.find(
                    eq("position.planet", origin.position.planet)
                )
            }
        }

        suspend fun create(planet: Planet, x: Int, y: Int, z: Int, name: String, crossPlanet: Boolean): Boolean {
            val galaxy = galaxyManager.get(null, planet.uuid) ?: return false

            val portal = Teleporter(
                UUID.randomUUID(),
                name,
                galaxy.uuid,
                Position(x.toDouble(), y.toDouble(), z.toDouble(), planet.uuid),
                crossPlanet
            )

            return collection.insertOne(portal).awaitFirstOrNull() != null
        }

        suspend fun update(teleporter: Teleporter) {
            collection.findOneAndReplace(eq("uuid", teleporter.uuid), teleporter).awaitFirstOrNull()
        }

        suspend fun remove(planet: Planet, x: Int, y: Int, z: Int): Boolean {
            val portal = get(planet, x, y, z) ?: return false

            return collection.deleteMany(eq("uuid", portal.uuid)).awaitFirst().deletedCount > 0
        }
    }
}
