package one.oktw.galaxy.types

import com.flowpowered.math.vector.Vector3d
import one.oktw.galaxy.annotation.Document
import java.util.*

@Document
data class Position(
    var x: Double,
    var y: Double,
    var z: Double,
    var planet: UUID? = null
) {
    constructor(vector3d: Vector3d, planet: UUID? = null) : this(vector3d.x, vector3d.y, vector3d.z, planet)

    fun fromVector3d(vector3d: Vector3d): Position {
        x = vector3d.x
        y = vector3d.y
        z = vector3d.z
        return this
    }

    fun toVector3d(): Vector3d {
        return Vector3d(x, y, z)
    }
}
