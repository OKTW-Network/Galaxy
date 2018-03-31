package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.ButtonType
import one.oktw.galaxy.enums.ItemType.BUTTON
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonProperty

@BsonDiscriminator
data class Button @BsonCreator constructor(val type: ButtonType) : Item {
    @BsonProperty("itemType")
    override val itemType = BUTTON
}
