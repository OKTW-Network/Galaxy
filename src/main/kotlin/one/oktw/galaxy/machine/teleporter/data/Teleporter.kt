package one.oktw.galaxy.machine.teleporter.data

import one.oktw.galaxy.Main
import one.oktw.galaxy.galaxy.planet.data.Position
import java.util.*

data class Teleporter constructor(
    val uuid: UUID = UUID.randomUUID(),
    val name: String = "Teleporter",
    val galaxy: UUID = Main.dummyUUID,
    val position: Position = Position(),
    val crossPlanet: Boolean = false
)
