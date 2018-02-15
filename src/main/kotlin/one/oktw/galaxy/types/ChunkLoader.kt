package one.oktw.galaxy.types

import java.util.*

data class ChunkLoader(
        val uuid: UUID = UUID.randomUUID(),
        val position: Position = Position(),
        var range: Short = 0
)
