package one.oktw.galaxy.types

import one.oktw.galaxy.enums.WeaponUpgradeType

data class Upgrade(
        val type: WeaponUpgradeType? = null,
        var level: Int = 1
)
