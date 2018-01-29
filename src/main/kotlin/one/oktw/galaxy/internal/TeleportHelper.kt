package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.internal.enums.AccessLevel.*
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class TeleportHelper {
    companion object {
        fun checkValid(player: Player, location: Location<World>): Boolean {
            val traveler = travelerManager.getTraveler(player)
            val planet = galaxyManager.getPlanet(location.extent.uniqueId) ?: return false

            return when (planet.checkPermission(traveler)) {
                MODIFY, VIEW -> true
                DENY -> false
            }
        }

        fun teleport(player: Player, location: Location<World>): Boolean {
            if (!checkValid(player, location)) return false

            val traveler = travelerManager.getTraveler(player)
            val planet = galaxyManager.getPlanet(location.extent.uniqueId) ?: return false

            if (PlanetHelper.loadPlanet(planet).isPresent) {
                val result = player.setLocation(location)

                if (result) {
                    traveler.position.fromPosition(location.position).planet = planet.uuid

                    if (planet.checkPermission(traveler) == VIEW) {
                        travelerManager.setViewer(player.uniqueId)
                    } else {
                        travelerManager.removeViewer(player.uniqueId)
                    }
                }

                return result
            }

            return false
        }

        fun teleportSafely(player: Player, location: Location<World>): Boolean {
            if (!checkValid(player, location)) return false

            val traveler = travelerManager.getTraveler(player)
            val planet = galaxyManager.getPlanet(location.extent.uniqueId) ?: return false

            if (PlanetHelper.loadPlanet(planet).isPresent) {
                val result = player.setLocationSafely(location)

                if (result) {
                    traveler.position.fromPosition(location.position).planet = planet.uuid

                    if (planet.checkPermission(traveler) == VIEW) {
                        travelerManager.setViewer(player.uniqueId)
                    } else {
                        travelerManager.removeViewer(player.uniqueId)
                    }
                }

                return result
            }

            return false
        }
    }
}