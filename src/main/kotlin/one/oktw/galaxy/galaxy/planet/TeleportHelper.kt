package one.oktw.galaxy.galaxy.planet

import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.enums.AccessLevel
import one.oktw.galaxy.enums.AccessLevel.DENY
import one.oktw.galaxy.enums.AccessLevel.VIEW
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.galaxy.planet.data.extensions.loadWorld
import one.oktw.galaxy.traveler.ViewerHelper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class TeleportHelper {
    companion object {
        suspend fun getAccess(player: Player, world: World): AccessLevel {
            val planet = galaxyManager.getPlanetFromWorld(world.uniqueId).await() ?: return DENY

            return planet.checkPermission(player)
        }

        suspend fun teleport(player: Player, planet: Planet): Boolean {
            return withContext(serverThread) { planet.loadWorld().orElse(null) }?.let { teleport(player, it) } ?: false
        }

        suspend fun teleport(player: Player, world: World): Boolean {
            val permission = getAccess(player, world)

            if (permission == DENY) return false
            if (!player.transferToWorld(world)) return false

            travelerManager.updateTraveler(player)

            if (galaxyManager.getPlanetFromWorld(world.uniqueId).await()?.checkPermission(player) == VIEW) {
                ViewerHelper.setViewer(player.uniqueId)
            } else {
                ViewerHelper.removeViewer(player.uniqueId)
            }

            return true
        }

        suspend fun teleport(player: Player, location: Location<World>, safety: Boolean = false): Boolean {
            val permission = getAccess(player, location.extent)

            if (permission == DENY) return false

            return (if (safety) player.setLocationSafely(location) else player.setLocation(location)).apply {
                travelerManager.updateTraveler(player)

                if (permission == VIEW) ViewerHelper.setViewer(player.uniqueId) else ViewerHelper.removeViewer(player.uniqueId)
            }
        }
    }
}
