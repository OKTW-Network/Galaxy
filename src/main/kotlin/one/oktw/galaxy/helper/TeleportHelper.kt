package one.oktw.galaxy.helper

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.Main.Companion.viewerManager
import one.oktw.galaxy.enums.AccessLevel.*
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class TeleportHelper {
    companion object {
        suspend fun checkValid(player: Player, location: Location<World>): Boolean {
            val planet = galaxyManager.getPlanetFromWorld(location.extent.uniqueId).await() ?: return false

            return when (planet.checkPermission(player)) {
                MODIFY, VIEW -> true
                DENY -> false
            }
        }

        suspend fun teleport(player: Player, location: Location<World>, safety: Boolean): Boolean {
            if (!checkValid(player, location)) return false

            galaxyManager.getPlanetFromWorld(location.extent.uniqueId).await()?.let {
                if (PlanetHelper.loadPlanet(it).isPresent) {
                    val result = if (safety) player.setLocationSafely(location) else player.setLocation(location)

                    if (result) {
                        travelerManager.updateTraveler(player)

                        if (it.checkPermission(player) == VIEW) {
                            viewerManager.setViewer(player.uniqueId)
                        } else {
                            viewerManager.removeViewer(player.uniqueId)
                        }
                    }

                    return result
                }
            }

            return false
        }
    }
}
