package one.oktw.galaxy.types

import one.oktw.galaxy.Main.Companion.travelerManager
import java.util.*

data class Traveler(
        val uuid: UUID? = null,
        var position: Position = Position(),
        var item: Item = Item()
) {
    fun save() {
        travelerManager.saveTraveler(this)
    }
}
