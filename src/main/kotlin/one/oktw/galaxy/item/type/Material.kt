package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.internal.LanguageService
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType.MATERIAL
import one.oktw.galaxy.item.enums.MaterialType
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes.STONE_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

@BsonDiscriminator
class Material(val type: MaterialType = MaterialType.DUMMY) : Item {
    //Todo check player lang
    val lang = LanguageService()
    override val itemType = MATERIAL

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(STONE_SWORD)
        .itemData(DataType(MATERIAL))
        .add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.WHITE, lang.getString("item.Material.${type.name}")))
        .add(Keys.UNBREAKABLE, true)
        .add(Keys.HIDE_UNBREAKABLE, true)
        .add(Keys.HIDE_MISCELLANEOUS, true)
        .add(Keys.HIDE_ATTRIBUTES, true)
        .add(Keys.HIDE_ENCHANTMENTS, true)
        .add(Keys.ITEM_DURABILITY, type.id)
        .build()
        .let(::removeDamage)
        .let(::removeCoolDown)
}
