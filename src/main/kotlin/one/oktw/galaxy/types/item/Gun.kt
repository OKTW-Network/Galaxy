package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.GunType
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import java.util.*

@BsonDiscriminator
data class Gun(
        override val uuid: UUID = UUID.randomUUID(),
        var type: GunType = GunType.PISTOL_ORIGIN,
        var maxTemp: Int = 100,
        var heat: Int = 10,
        var cooling: Int = 1,
        var range: Double = 10.0,
        var damage: Double = 3.0,
        var through: Int = 1,
        var slot: Int = 1,
        var upgrade: ArrayList<Upgrade> = ArrayList()
) : IItem, ICoolDown
