package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataOverheat
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ItemType.PISTOL
import one.oktw.galaxy.item.enums.ItemType.SNIPER
import one.oktw.galaxy.item.gun.GunStyle
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
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
) : Item, Overheat {
    override fun createItemStack(): ItemStack {
        val item = ItemStack.builder()
            .itemType(ItemTypes.DIAMOND_SWORD)
            .itemData(DataUUID.Immutable(uuid))
            .itemData(DataOverheat())
            .add(Keys.UNBREAKABLE, true)
            .add(Keys.HIDE_UNBREAKABLE, true)
            .add(Keys.HIDE_MISCELLANEOUS, true)
            .add(Keys.HIDE_ATTRIBUTES, true)
            .add(Keys.HIDE_ENCHANTMENTS, true)
            .add(Keys.ITEM_DURABILITY, style.id.toInt())

        when (itemType) {
            PISTOL -> item.add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.AQUA, "Laser Gun"))
            SNIPER -> {
                item.itemData(DataEnable())
                item.add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.GOLD, "Sniper"))
            }
            else -> Unit
        }

        return item.build().let(::removeDamage).let(::removeCoolDown)
    }
}
