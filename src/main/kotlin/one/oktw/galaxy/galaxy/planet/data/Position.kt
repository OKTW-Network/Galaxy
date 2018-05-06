package one.oktw.galaxy.galaxy.planet.data

import com.flowpowered.math.vector.Vector3d
import org.bson.codecs.pojo.annotations.BsonCreator
import java.util.*

data class Position @BsonCreator constructor(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var planet: UUID? = null
) {
    constructor(vector3d: Vector3d, planet: UUID? = null) : this(vector3d.x, vector3d.y, vector3d.z, planet)
}
