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

import com.flowpowered.math.vector.Vector3d
import one.oktw.galaxy.galaxy.planet.data.Position

fun Position.fromVector3d(vector3d: Vector3d): Position {
    x = vector3d.x
    y = vector3d.y
    z = vector3d.z
    return this
}

fun Position.toVector3d(): Vector3d {
    return Vector3d(x, y, z)
}
