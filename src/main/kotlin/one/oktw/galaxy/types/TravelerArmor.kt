package one.oktw.galaxy.types

import one.oktw.galaxy.annotation.Document

@Document
data class TravelerArmor(
    var shield: Int = 0,
    var quick: Int = 0,
    var adapt: Int = 0,
    var fly: Boolean = false
)
