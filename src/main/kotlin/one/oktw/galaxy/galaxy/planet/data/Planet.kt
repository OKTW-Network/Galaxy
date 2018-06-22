package one.oktw.galaxy.galaxy.planet.data

import one.oktw.galaxy.Main.Companion.dummyUUID
import java.util.*

data class Planet(
    val uuid: UUID = UUID.randomUUID(),
    var world: UUID = dummyUUID,
    var name: String = "",
    var size: Int = 32,
    var visitable: Boolean = true,
    var lastTime: Date = Date()
)
