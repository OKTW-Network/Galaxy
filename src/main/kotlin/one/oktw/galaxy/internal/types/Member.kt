package one.oktw.galaxy.internal.types

import one.oktw.galaxy.internal.enums.Group
import java.util.*

data class Member(
        val uuid: UUID? = null,
        var group: Group = Group.MEMBER
)
