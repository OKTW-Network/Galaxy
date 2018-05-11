package one.oktw.galaxy.galaxy.planet.data

import one.oktw.galaxy.enums.SecurityLevel
import one.oktw.galaxy.enums.SecurityLevel.VISIT
import java.util.*

data class Planet(
    val uuid: UUID = UUID.randomUUID(),
    var world: UUID? = null,
    var name: String = "",
    var size: Int = 32,
    var security: SecurityLevel = VISIT,
    var lastTime: Date = Date()
)
