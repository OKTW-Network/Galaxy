package one.oktw.galaxy.galaxy.planet

import kotlinx.coroutines.experimental.async
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.galaxy.planet.data.extensions.loadWorld
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel.DENY
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel.VIEW
import one.oktw.galaxy.player.event.Viewer.Companion.removeViewer
import one.oktw.galaxy.player.event.Viewer.Companion.setViewer
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class TeleportHelper {
    companion object {
        suspend fun getAccess(player: Player, world: World): AccessLevel {
            val planet = galaxyManager.get(world).await()?.getPlanet(world) ?: return DENY

            return planet.checkPermission(player)
        }

        fun teleport(player: Player, planet: Planet) = async(serverThread) {
            planet.loadWorld()?.let { teleport(player, it) } ?: false
        }


        suspend fun teleport(player: Player, world: World): Boolean {
            if (getAccess(player, world) == DENY) return false
            if (!player.transferToWorld(world)) return false

            if (galaxyManager.get(world).await()?.getPlanet(world)?.checkPermission(player) == VIEW) {
                setViewer(player.uniqueId)
            } else {
                removeViewer(player.uniqueId)
            }

            return true
        }

        suspend fun teleport(player: Player, location: Location<World>, safety: Boolean = false): Boolean {
            val permission = getAccess(player, location.extent)

            if (permission == DENY) return false
            if (!if (safety) player.setLocationSafely(location) else player.setLocation(location)) return false

            if (permission == VIEW) setViewer(player.uniqueId) else removeViewer(player.uniqueId)

            return true
        }
    }
}
