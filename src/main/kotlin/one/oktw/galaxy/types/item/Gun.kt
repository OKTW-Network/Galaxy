package one.oktw.galaxy.types.item

import one.oktw.galaxy.annotation.Document
import one.oktw.galaxy.enums.ItemType
import one.oktw.galaxy.enums.ItemType.PISTOL
import one.oktw.galaxy.item.gun.GunStyle
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import java.util.*

/**
 * Gun data class
 *
 * @property uuid UUID
 * @property itemType Item type
 * @property style Gun style
 * @property maxTemp Max temp
 * @property heat heat pre use
 * @property cooling cooling pre tick
 * @property range Gun range
 * @property damage Gun Damage
 * @property through Max damage entity number
 * @property upgrade Upgrade list
 */
@Document
@BsonDiscriminator
data class Gun(
    override val uuid: UUID = UUID.randomUUID(),
    override val itemType: ItemType = PISTOL,
    override var maxTemp: Int,
    override var heat: Int,
    override var cooling: Int = 1,
    var style: GunStyle,
    var range: Double,
    var damage: Double,
    var through: Int = 1,
    var upgrade: ArrayList<Upgrade> = ArrayList()
) : Item, Overheat
