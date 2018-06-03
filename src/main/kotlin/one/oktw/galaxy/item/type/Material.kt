package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.internal.LanguageService
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType.MATERIAL
import one.oktw.galaxy.item.enums.MaterialType
import one.oktw.galaxy.item.enums.MaterialType.DUMMY
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes.STONE_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.WHITE
import org.spongepowered.api.text.format.TextStyles.BOLD

@BsonDiscriminator
data class Material(val type: MaterialType = DUMMY) : Item {
    //Todo check player lang
    val lang = LanguageService()
    override val itemType = MATERIAL

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(STONE_SWORD)
        .itemData(DataType(MATERIAL))
        .add(DISPLAY_NAME, Text.of(BOLD, WHITE, lang.getString("item.Material.${type.name}")))
        .add(UNBREAKABLE, true)
        .add(HIDE_UNBREAKABLE, true)
        .add(HIDE_MISCELLANEOUS, true)
        .add(HIDE_ATTRIBUTES, true)
        .add(HIDE_ENCHANTMENTS, true)
        .add(ITEM_DURABILITY, type.id)
        .build()
        .let(::removeDamage)
        .let(::removeCoolDown)

    override fun test(item: ItemStack): Boolean {
        return item[DataType.key].orElse(null) == MATERIAL && item[ITEM_DURABILITY].orElse(null) == type.id
    }

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
