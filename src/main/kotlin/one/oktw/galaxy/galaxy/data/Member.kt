package one.oktw.galaxy.galaxy.data

import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.Group.MEMBER
import org.bson.codecs.pojo.annotations.BsonCreator
import java.util.*

data class Member @BsonCreator constructor(
    val uuid: UUID? = null,
    var group: Group = MEMBER
)
