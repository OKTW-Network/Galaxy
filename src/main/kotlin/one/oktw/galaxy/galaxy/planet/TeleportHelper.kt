package one.oktw.galaxy.galaxy.planet

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.enums.AccessLevel.*
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.galaxy.planet.data.extensions.loadWorld
import one.oktw.galaxy.traveler.ViewerHelper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class TeleportHelper {
    companion object {
        suspend fun checkValid(player: Player, world: World): Boolean {
            val planet = galaxyManager.getPlanetFromWorld(world.uniqueId).await() ?: return false

            return when (planet.checkPermission(player)) {
                MODIFY, VIEW -> true
                DENY -> false
            }
        }

        suspend fun checkValid(player: Player, location: Location<World>) =
            checkValid(player, location.extent)

        suspend fun teleport(player: Player, world: World): Boolean {
            if (!checkValid(player, world)) return false
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

                return result
            }

            return false
        }
    }
}
