package one.oktw.galaxy.item.type

import one.oktw.galaxy.Main.Companion.languageService
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
 * @property heat heat per use
 * @property cooling cooling per tick
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
        val lang = languageService.getDefaultLanguage() // TODO player lang
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
            PISTOL -> item.add(DISPLAY_NAME, Text.of(BOLD, GREEN, lang["item.Gun.PISTOL"]))
            SNIPER -> {
                item.itemData(DataEnable())
                item.add(DISPLAY_NAME, Text.of(BOLD, GREEN, lang["item.Gun.SNIPER"]))
            }
            else -> Unit
        }

        return item.build().let(::removeDamage).let(::removeCoolDown)
    }

    override fun test(t: ItemStack) = false // TODO

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
