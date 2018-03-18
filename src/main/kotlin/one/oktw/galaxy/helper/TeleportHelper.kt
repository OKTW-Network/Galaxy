package one.oktw.galaxy.helper

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
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

        suspend fun teleport(player: Player, location: Location<World>, safety: Boolean = false): Boolean {
            if (!checkValid(player, location)) return false

            galaxyManager.getPlanetFromWorld(location.extent.uniqueId).await()?.let {
                it.loadWorld().orElse(null) ?: return@let

                val result = if (safety) player.setLocationSafely(location) else player.setLocation(location)
                if (!result) return@let

                travelerManager.updateTraveler(player)
                if (it.checkPermission(player) == VIEW) {
                    ViewerHelper.setViewer(player.uniqueId)
                } else {
                    ViewerHelper.removeViewer(player.uniqueId)
                }
            }

            return false
        }
    }
}
