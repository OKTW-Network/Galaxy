package one.oktw.galaxy.types

import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.Group.MEMBER
import java.util.*

data class Member(
        val uuid: UUID? = null,
        var group: Group = MEMBER
)
