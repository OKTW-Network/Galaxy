package one.oktw.galaxy.galaxy.traveler.data

import one.oktw.galaxy.economy.StarDustKeeper
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.galaxy.planet.data.Position
import one.oktw.galaxy.item.type.Item
import one.oktw.galaxy.item.type.Upgrade
import java.util.*
import kotlin.collections.ArrayList

data class Traveler(
    val uuid: UUID = throw NullPointerException(),
    var group: Group = VISITOR,
    var position: Position = Position(),
    var armor: ArrayList<Upgrade> = ArrayList(),
    var item: ArrayList<Item> = ArrayList()
) : StarDustKeeper()
