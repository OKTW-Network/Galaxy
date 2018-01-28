package one.oktw.galaxy.internal.types

import one.oktw.galaxy.Main.Companion.travelerManager
import java.util.*

data class Traveler(
        val uuid: UUID? = null,
        var armor: Armor = Armor(),
        var position: Position = Position()
) {
    fun save() {
        travelerManager.saveTraveler(this)
    }
}
