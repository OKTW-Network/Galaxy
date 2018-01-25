package one.oktw.galaxy.internal.types

import java.util.*
import kotlin.collections.ArrayList

data class Galaxy(
        val uuid: UUID = UUID.randomUUID(),
        var name: String? = null,
        var members: List<Member> = ArrayList(),
        var planets: List<Planet> = ArrayList()
)