package one.oktw.galaxy.traveler.data

import one.oktw.galaxy.economy.StarDustKeeper
import one.oktw.galaxy.galaxy.planet.data.Position
import one.oktw.galaxy.item.type.Item
import one.oktw.galaxy.item.type.Upgrade
import java.util.*
import kotlin.collections.ArrayList

data class Traveler(
    val uuid: UUID? = null,
    var position: Position = Position(),
    var armor: ArrayList<Upgrade> = ArrayList(),
    var item: ArrayList<Item> = ArrayList()
) : StarDustKeeper()
