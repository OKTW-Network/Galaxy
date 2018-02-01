package one.oktw.galaxy.internal.types

import one.oktw.galaxy.internal.types.item.Armor
import one.oktw.galaxy.internal.types.item.Gun

data class Item(
        val armor: Armor = Armor(),
        var gun: Gun? = null
)