package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.ItemType
import one.oktw.galaxy.enums.ItemType.UPGRADE
import one.oktw.galaxy.enums.UpgradeType
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonProperty

@BsonDiscriminator
data class Upgrade @BsonCreator constructor(
    @BsonProperty("type") val type: UpgradeType,
    @BsonProperty("level") var level: Int,
    @BsonProperty("itemType") override val itemType: ItemType = UPGRADE
) : Item
