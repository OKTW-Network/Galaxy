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

package one.oktw.galaxy.galaxy.planet.data.extensions

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.galaxy.data.extensions.getGroup
import one.oktw.galaxy.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel.*
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.World

suspend fun Planet.checkPermission(player: Player): AccessLevel {
    val group = galaxyManager.get(planet = uuid)?.getGroup(player) ?: VISITOR

    return if (group != VISITOR) MODIFY else if (visitable) VIEW else DENY
}

suspend fun Planet.loadWorld(): World? {
    return PlanetHelper.loadPlanet(this)
}
