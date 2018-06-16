package one.oktw.galaxy.item.type

import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataOverheat
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ItemType.SWORD
import one.oktw.galaxy.item.enums.SwordType
import one.oktw.galaxy.item.enums.SwordType.*
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.GREEN
import org.spongepowered.api.text.format.TextStyles.BOLD
import java.util.*

/**
 * Sword data class
 *
 * @property uuid UUID
 * @property itemType Item type
 * @property type Sword type
 * @property maxTemp Max temp
 * @property heat heat per use
 * @property cooling cooling per tick
 * @property range Sword range
 * @property damage Sword Damage
 * @property upgrade Upgrade list
 */
@BsonDiscriminator
data class Sword(
    override val uuid: UUID = UUID.randomUUID(),
    override val itemType: ItemType = SWORD,
    override var maxTemp: Int = 0,
    override var heat: Int = 0,
    override var cooling: Int = 1,
    var type: SwordType = MAGI,
    var AOE_damage: Double = 0.0,
    var damage: Double = 0.0,
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
            .add(ITEM_DURABILITY, type.id.toInt())
            .itemData(DataEnable())

        when (type) {
            MAGI -> item.add(DISPLAY_NAME, Text.of(BOLD, GREEN, lang["item.sword.MAGI"]))
            FIRE -> item.add(DISPLAY_NAME, Text.of(BOLD, GREEN, lang["item.sword.FIRE"]))
            SCABBARD -> item.add(DISPLAY_NAME, Text.of(BOLD, GREEN, lang["item.sword.SCABBARD"]))
        }

        return item.build().let(::removeDamage).let(::removeCoolDown)
    }

    override fun test(t: ItemStack) = false // TODO

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
