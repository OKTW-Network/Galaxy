package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.UpgradeType
import one.oktw.galaxy.enums.UpgradeType.EMPTY
import org.bson.codecs.pojo.annotations.BsonDiscriminator

@BsonDiscriminator
data class Upgrade(
        val type: UpgradeType = EMPTY,
        var level: Int = 1
) : IItem
