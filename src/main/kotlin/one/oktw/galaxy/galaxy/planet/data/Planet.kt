package one.oktw.galaxy.galaxy.planet.data

import java.util.*

data class Planet(
    val uuid: UUID = UUID.randomUUID(),
    var world: UUID? = null,
    var name: String = "",
    var size: Int = 32,
    var visitable: Boolean = true,
    var lastTime: Date = Date()
)
