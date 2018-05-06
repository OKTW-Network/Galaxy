package one.oktw.galaxy.galaxy.planet.data

import one.oktw.galaxy.enums.SecurityLevel
import one.oktw.galaxy.enums.SecurityLevel.VISIT
import org.bson.codecs.pojo.annotations.BsonCreator
import java.util.*

data class Planet @BsonCreator constructor(
    val uuid: UUID = UUID.randomUUID(),
    var world: UUID? = null,
    var name: String = "",
    var size: Int = 32,
    var security: SecurityLevel = VISIT,
    var lastTime: Date = Date()
)
