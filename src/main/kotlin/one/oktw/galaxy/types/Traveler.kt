package one.oktw.galaxy.types

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.types.item.ItemBase
import java.util.*
import kotlin.collections.ArrayList

data class Traveler(
        val uuid: UUID? = null,
        var position: Position = Position(),
        var armor: Armor = Armor(),
        var item: ArrayList<ItemBase> = ArrayList()
) {
    fun save() {
        travelerManager.saveTraveler(this)
    }
}
