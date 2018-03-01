package one.oktw.galaxy.types

import one.oktw.galaxy.types.item.Upgrade
import java.util.*
import kotlin.collections.ArrayList

data class ChunkLoader(
    val uuid: UUID = UUID.randomUUID(),
    val position: Position = Position(),
    var upgrade: ArrayList<Upgrade> = ArrayList()
)
