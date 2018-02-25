package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.UpgradeType
import one.oktw.galaxy.enums.UpgradeType.EMPTY

data class ItemUpgrade(
        val type: UpgradeType = EMPTY,
        var level: Int = 1
) : ItemBase
