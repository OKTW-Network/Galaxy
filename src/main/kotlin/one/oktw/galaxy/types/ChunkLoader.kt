package one.oktw.galaxy.types

import one.oktw.galaxy.types.item.Upgrade
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*
import kotlin.collections.ArrayList

data class ChunkLoader @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID = UUID.randomUUID(),
    @BsonProperty("position") val position: Position,
    @BsonProperty("upgrade") var upgrade: ArrayList<Upgrade> = ArrayList()
)
