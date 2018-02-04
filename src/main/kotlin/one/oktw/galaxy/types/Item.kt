package one.oktw.galaxy.types

import one.oktw.galaxy.types.item.Armor
import one.oktw.galaxy.types.item.Gun

data class Item(
        val armor: Armor = Armor(),
        var gun: ArrayList<Gun> = ArrayList()
)