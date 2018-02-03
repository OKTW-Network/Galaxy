package one.oktw.galaxy.types.item

import one.oktw.galaxy.types.Upgrade

data class Gun(
        var coolDown: Double = 5.0,
        var range: Double = 10.0,
        var damage: Double = 3.0,
        var slot: Int = 1,
        var upgrade: List<Upgrade> = ArrayList()
)