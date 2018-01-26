package one.oktw.galaxy.internal.types

import one.oktw.galaxy.internal.TravelerManager
import java.util.*

data class Traveler(
        val uuid: UUID? = null,
        var armor: Armor = Armor(),
        var position: Position = Position()
) {
    fun save() {
        TravelerManager.saveTraveler(this)
    }
}