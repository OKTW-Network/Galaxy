package one.oktw.galaxy.types

import one.oktw.galaxy.annotation.Document
import one.oktw.galaxy.types.item.Upgrade
import java.util.*
import kotlin.collections.ArrayList

@Document
data class ChunkLoader(
    val uuid: UUID = UUID.randomUUID(),
    val position: Position,
    var upgrade: ArrayList<Upgrade> = ArrayList()
)
