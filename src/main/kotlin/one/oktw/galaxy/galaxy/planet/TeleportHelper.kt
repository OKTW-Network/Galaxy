package one.oktw.galaxy.galaxy.planet

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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

        fun teleport(player: Player, world: World) = async(serverThread) {
            if (getAccess(player, world) == DENY) return@async false
            if (!player.transferToWorld(world)) return@async false

            launch {
                if (galaxyManager.get(world).await()?.getPlanet(world)?.checkPermission(player) == VIEW) {
                    setViewer(player.uniqueId)
                } else {
                    removeViewer(player.uniqueId)
                }
            }

            return@async true
        }

        fun teleport(player: Player, location: Location<World>, safety: Boolean = false) = async(serverThread) {
            val permission = getAccess(player, location.extent)

            if (permission == DENY) return@async false
            if (!if (safety) player.setLocationSafely(location) else player.setLocation(location)) return@async false

            if (permission == VIEW) setViewer(player.uniqueId) else removeViewer(player.uniqueId)

            return@async true
        }
    }
}
