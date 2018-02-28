package one.oktw.galaxy.types.item

import java.util.*

interface ICoolable {
    val uuid: UUID
    var maxTemp: Int
    var heat: Int
    var cooling: Int
}