package one.oktw.galaxy.galaxy.data

import one.oktw.galaxy.economy.StarDustKeeper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import java.util.*
import kotlin.collections.ArrayList

data class Galaxy(
    val uuid: UUID = UUID.randomUUID(),
    var name: String = "",
    var info: String = "",
    var notice: String = "",
    val members: ArrayList<Traveler> = ArrayList(),
    val planets: ArrayList<Planet> = ArrayList(),
    val joinRequest: ArrayList<UUID> = ArrayList()
) : StarDustKeeper() {
    init {
        interestRate = 0.0004
    }
}
