package one.oktw.galaxy.types

import one.oktw.galaxy.enums.UpgradeType

data class Upgrade(
        val type: UpgradeType? = null,
        var level: Int = 1
)