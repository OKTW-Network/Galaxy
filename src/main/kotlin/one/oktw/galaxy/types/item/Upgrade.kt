package one.oktw.galaxy.types.item

import one.oktw.galaxy.annotation.Document
import one.oktw.galaxy.enums.ItemType
import one.oktw.galaxy.enums.ItemType.UPGRADE
import one.oktw.galaxy.enums.UpgradeType
import org.bson.codecs.pojo.annotations.BsonDiscriminator

@Document
@BsonDiscriminator
data class Upgrade(
    val type: UpgradeType,
    var level: Int,
    override val itemType: ItemType = UPGRADE
) : Item
