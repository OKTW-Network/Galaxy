package one.oktw.galaxy.types

import com.flowpowered.math.vector.Vector3d
import java.util.*

data class Position(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var z: Double = 0.0,
        var planet: UUID? = null
) {
    fun fromPosition(vector3d: Vector3d): Position {
        x = vector3d.x
        y = vector3d.y
        z = vector3d.z
        return this
    }
}
