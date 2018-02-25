package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.UpgradeType
import one.oktw.galaxy.enums.UpgradeType.EMPTY
import java.util.*

data class Upgrade(
        override val uuid: UUID = UUID.randomUUID(),
        val type: UpgradeType = EMPTY,
        var level: Int = 1
) : ItemBase
