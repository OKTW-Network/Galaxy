/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.galaxy.planet

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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
            val planet = galaxyManager.get(world)?.getPlanet(world) ?: return DENY

            return planet.checkPermission(player)
        }

        fun teleport(player: Player, planet: Planet) = GlobalScope.async(serverThread) {
            planet.loadWorld()?.let { teleport(player, it).await() } ?: false
        }

        fun teleport(player: Player, world: World) = GlobalScope.async(serverThread) {
            if (getAccess(player, world) == DENY) return@async false

            return@async player.transferToWorld(world)
        }

        fun teleport(player: Player, location: Location<World>, safety: Boolean = false) = GlobalScope.async(serverThread) {
            val permission = getAccess(player, location.extent)

            if (permission == DENY) return@async false
            if (!if (safety) player.setLocationSafely(location) else player.setLocation(location)) return@async false

            if (permission == VIEW) setViewer(player.uniqueId) else removeViewer(player.uniqueId)

            return@async true
        }
    }
}
