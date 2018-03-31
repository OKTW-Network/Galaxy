package one.oktw.galaxy.types

import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.Group.MEMBER
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*

data class Member @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID,
    @BsonProperty("group") var group: Group = MEMBER
)
