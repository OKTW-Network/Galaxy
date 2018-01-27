package one.oktw.galaxy.internal.types

import com.flowpowered.math.vector.Vector3d
import java.util.*

data class Position(
        var x: Double? = null,
        var y: Double? = null,
        var z: Double? = null,
        var planet: UUID? = null
) {
    fun fromPosition(vector3d: Vector3d): Position {
        x = vector3d.x
        y = vector3d.y
        z = vector3d.z
        return this
    }
}
