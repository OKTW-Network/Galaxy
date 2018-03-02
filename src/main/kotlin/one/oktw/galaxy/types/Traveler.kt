package one.oktw.galaxy.types

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.annotation.Document
import one.oktw.galaxy.types.item.IItem
import java.util.*
import kotlin.collections.ArrayList

@Document
data class Traveler(
    val uuid: UUID,
    var position: Position,
    var armor: Armor = Armor(),
    var item: ArrayList<IItem> = ArrayList()
) {
    fun save() {
        travelerManager.saveTraveler(this)
    }
}
