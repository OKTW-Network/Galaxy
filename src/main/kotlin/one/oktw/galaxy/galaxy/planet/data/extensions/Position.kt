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