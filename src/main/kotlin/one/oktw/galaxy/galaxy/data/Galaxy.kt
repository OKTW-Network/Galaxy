package one.oktw.galaxy.galaxy.data

import one.oktw.galaxy.galaxy.planet.data.Planet
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*
import kotlin.collections.ArrayList

data class Galaxy @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID = UUID.randomUUID(),
    @BsonProperty("name") var name: String,
    @BsonProperty("members") val members: ArrayList<Member> = ArrayList(),
    @BsonProperty("planets") val planets: ArrayList<Planet> = ArrayList(),
    @BsonProperty("joinRequest") val joinRequest: ArrayList<UUID> = ArrayList()
)
