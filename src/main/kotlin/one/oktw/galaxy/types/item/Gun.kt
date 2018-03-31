package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.ItemType
import one.oktw.galaxy.enums.ItemType.PISTOL
import one.oktw.galaxy.item.gun.GunStyle
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonProperty
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
@BsonDiscriminator
data class Gun @BsonCreator constructor(
    @BsonProperty("uuid") override val uuid: UUID = UUID.randomUUID(),
    @BsonProperty("itemType") override val itemType: ItemType = PISTOL, // TODO split to different class
    @BsonProperty("maxTemp") override var maxTemp: Int,
    @BsonProperty("heat") override var heat: Int,
    @BsonProperty("cooling") override var cooling: Int = 1,
    @BsonProperty("style") var style: GunStyle,
    @BsonProperty("range") var range: Double,
    @BsonProperty("damage") var damage: Double,
    @BsonProperty("through") var through: Int = 1,
    @BsonProperty("upgrade") var upgrade: ArrayList<Upgrade> = ArrayList()
) : Item, Overheat
