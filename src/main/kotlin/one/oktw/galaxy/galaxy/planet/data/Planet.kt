package one.oktw.galaxy.galaxy.planet.data

import one.oktw.galaxy.enums.SecurityLevel
import one.oktw.galaxy.enums.SecurityLevel.VISIT
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*

data class Planet @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID = UUID.randomUUID(),
    @BsonProperty("world") var world: UUID,
    @BsonProperty("name") var name: String,
    @BsonProperty("size") var size: Int = 32,
    @BsonProperty("security") var security: SecurityLevel = VISIT,
    @BsonProperty("lastTime") var lastTime: Date = Date()
)
