package one.oktw.galaxy.types

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.types.item.Item
import one.oktw.galaxy.types.item.Upgrade
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*
import kotlin.collections.ArrayList

data class Traveler @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID,
    @BsonProperty("position") var position: Position,
    @BsonProperty("armor") var armor: ArrayList<Upgrade> = ArrayList(),
    @BsonProperty("item") var item: ArrayList<Item> = ArrayList()
) {
    fun save() {
        travelerManager.saveTraveler(this)
    }
}
