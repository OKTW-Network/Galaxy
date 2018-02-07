package one.oktw.galaxy.types.item

import org.bson.codecs.pojo.annotations.BsonDiscriminator
import java.util.*

@BsonDiscriminator
interface ItemBase {
    val uuid: UUID
}
