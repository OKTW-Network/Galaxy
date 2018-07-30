package one.oktw.galaxy.machine.transporter.data

import one.oktw.galaxy.Main
import one.oktw.galaxy.galaxy.planet.data.Position
import java.util.*

data class Transporter constructor(
    val uuid: UUID = UUID.randomUUID(),
    val name: String = "Transporter",
    val galaxy: UUID = Main.dummyUUID,
    val position: Position = Position(),
    val crossPlanet: Boolean = false
)
