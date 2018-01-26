package one.oktw.galaxy.internal.types

import one.oktw.galaxy.internal.enums.SecurityLevel
import java.util.*

data class Planet(
        val uuid: UUID = UUID.randomUUID(),
        var world: UUID? = null,
        var name: String? = null,
        var size: Int = 32,
        var security: SecurityLevel = SecurityLevel.VISIT,
        var lastTime: Date = Date()
)
