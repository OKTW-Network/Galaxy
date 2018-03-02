package one.oktw.galaxy.types.item

import org.bson.codecs.pojo.annotations.BsonDiscriminator
import java.util.*

@BsonDiscriminator
interface ICoolable {
    val uuid: UUID
    var maxTemp: Int
    var heat: Int
    var cooling: Int
}