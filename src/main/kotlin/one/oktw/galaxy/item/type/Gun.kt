package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataOverheat
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.GunStyle
import one.oktw.galaxy.item.enums.GunStyle.PISTOL_ORIGIN
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ItemType.PISTOL
import one.oktw.galaxy.item.enums.ItemType.SNIPER
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.GREEN
import org.spongepowered.api.text.format.TextStyles.BOLD
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
data class Gun(
    override val uuid: UUID = UUID.randomUUID(),
    override val itemType: ItemType = PISTOL, // TODO split to different class
    override var maxTemp: Int = 0,
    override var heat: Int = 0,
    override var cooling: Int = 1,
    var style: GunStyle = PISTOL_ORIGIN,
    var range: Double = 0.0,
    var damage: Double = 0.0,
    var through: Int = 1,
    var upgrade: ArrayList<Upgrade> = ArrayList()
) : Item, Overheat {
    override fun createItemStack(): ItemStack {
        val item = ItemStack.builder()
            .itemType(ItemTypes.DIAMOND_SWORD)
            .itemData(DataUUID.Immutable(uuid))
            .itemData(DataOverheat())
            .add(UNBREAKABLE, true)
            .add(HIDE_UNBREAKABLE, true)
            .add(HIDE_MISCELLANEOUS, true)
            .add(HIDE_ATTRIBUTES, true)
            .add(HIDE_ENCHANTMENTS, true)
            .add(ITEM_DURABILITY, style.id.toInt())

        when (itemType) {
            PISTOL -> item.add(DISPLAY_NAME, Text.of(BOLD, GREEN, "Laser Gun"))
            SNIPER -> {
                item.itemData(DataEnable())
                item.add(DISPLAY_NAME, Text.of(BOLD, GREEN, "Sniper"))
            }
            else -> Unit
        }

        return item.build().let(::removeDamage).let(::removeCoolDown)
    }

    override fun test(t: ItemStack) = false // TODO

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
