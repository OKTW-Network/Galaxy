package one.oktw.galaxy.machine.portal.data

import one.oktw.galaxy.Main
import one.oktw.galaxy.galaxy.planet.data.Position
import java.util.*

data class Portal constructor(
    val uuid: UUID = UUID.randomUUID(),
    val name: String = "Portal",
    val galaxy: UUID = Main.dummyUUID,
    val position: Position = Position(),
    val crossPlanet: Boolean = false
)
