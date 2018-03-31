package one.oktw.galaxy.types

import com.flowpowered.math.vector.Vector3d
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*

data class Position @BsonCreator constructor(
    @BsonProperty("x") var x: Double,
    @BsonProperty("y") var y: Double,
    @BsonProperty("z") var z: Double,
    @BsonProperty("planet") var planet: UUID? = null
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
