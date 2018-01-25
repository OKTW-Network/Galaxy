package one.oktw.galaxy.internal.types

import java.util.*

data class Planet(
        val uuid: UUID = UUID.randomUUID(),
        var world: UUID? = null,
        var name: String? = null,
        var size: Int = 32,
        var security: SecurityLevel = SecurityLevel.VISIT
)