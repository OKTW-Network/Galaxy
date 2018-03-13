package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.ItemType
import org.bson.codecs.pojo.annotations.BsonDiscriminator

@BsonDiscriminator
interface Item {
    val itemType: ItemType
}
