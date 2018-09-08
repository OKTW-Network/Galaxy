package one.oktw.galaxy.command.data

import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.planet.data.Planet
import java.util.*

data class GalaxyAndPlanet(val galaxy: Galaxy, val planet: Planet, val planetUUID: UUID)
