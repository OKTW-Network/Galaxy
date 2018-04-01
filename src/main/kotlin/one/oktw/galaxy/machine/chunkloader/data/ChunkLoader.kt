package one.oktw.galaxy.machine.chunkloader.data

import one.oktw.galaxy.item.type.Upgrade
import one.oktw.galaxy.types.Position
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*
import kotlin.collections.ArrayList

data class ChunkLoader @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID = UUID.randomUUID(),
    @BsonProperty("position") val position: Position,
    @BsonProperty("upgrade") var upgrade: ArrayList<Upgrade> = ArrayList()
)
