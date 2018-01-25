package one.oktw.galaxy.internal.types

import java.util.*

data class Member(
        val uuid: UUID? = null,
        var group: Groups = Groups.MEMBER
)
