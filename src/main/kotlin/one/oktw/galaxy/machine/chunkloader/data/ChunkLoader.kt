package one.oktw.galaxy.machine.chunkloader.data

import one.oktw.galaxy.galaxy.planet.data.Position
import one.oktw.galaxy.item.type.Upgrade
import java.util.*
import kotlin.collections.ArrayList

data class ChunkLoader(
    val uuid: UUID = UUID.randomUUID(),
    val position: Position = Position(),
    var upgrade: ArrayList<Upgrade> = ArrayList()
)
