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

package one.oktw.galaxy.galaxy.planet.event

import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.util.AABB

class SpawnProtect {
    @Listener
    fun onChangeBlock(event: ChangeBlockEvent) {
        event.filter {
            !it.extent.spawnLocation.blockPosition.run { AABB(add(2, 3, 2), sub(2, 1, 2)) }.contains(it.blockPosition)
        }
    }

    @Listener
    fun onChangeBlock(event: ChangeBlockEvent.Pre) {
        val aabb = event.locations.firstOrNull()?.extent?.spawnLocation?.blockPosition
            ?.run { AABB(add(2, 3, 2), sub(2, 1, 2)) } ?: return

        if (event.locations.any { aabb.contains(it.blockPosition) }) event.isCancelled = true
    }
}
