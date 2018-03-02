package one.oktw.galaxy.types

import one.oktw.galaxy.annotation.Document
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.Group.MEMBER
import java.util.*

@Document
data class Member(
    val uuid: UUID,
    var group: Group = MEMBER
)
