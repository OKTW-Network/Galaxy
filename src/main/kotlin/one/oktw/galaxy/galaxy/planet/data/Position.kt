package one.oktw.galaxy.galaxy.planet.data

import com.flowpowered.math.vector.Vector3d
import java.util.*

data class Position(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var planet: UUID? = null
) {
    constructor(vector3d: Vector3d, planet: UUID? = null) : this(vector3d.x, vector3d.y, vector3d.z, planet)
}
