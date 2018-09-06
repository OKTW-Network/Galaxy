package one.oktw.galaxy.item.type

import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.translationService
import one.oktw.galaxy.data.DataItemType
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
    val lang = Main.translationService

    override val itemType = MATERIAL

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(STONE_SWORD)
        .itemData(DataItemType(MATERIAL))
        .add(DISPLAY_NAME, lang.ofPlaceHolder(BOLD, WHITE, lang.translation("item.Material.${type.name}")))
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
        return item[DataItemType.key].orElse(null) == MATERIAL && item[ITEM_DURABILITY].orElse(null) == type.id
    }

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
