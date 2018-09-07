package one.oktw.galaxy.item.type

import one.oktw.galaxy.Main
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType.TOOL
import one.oktw.galaxy.item.enums.ToolType
import one.oktw.galaxy.item.enums.ToolType.DUMMY
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes.IRON_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.format.TextColors.YELLOW
import org.spongepowered.api.text.format.TextStyles.BOLD

@BsonDiscriminator
data class Tool(val type: ToolType = DUMMY) : Item {
    val lang = Main.translationService

    override val itemType = TOOL

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(IRON_SWORD)
        .itemData(DataItemType(TOOL))
        .add(DISPLAY_NAME, lang.ofPlaceHolder(BOLD, YELLOW, lang.of("item.Tool.${type.name}")))
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
        return item[DataItemType.key].orElse(null) == TOOL && item[ITEM_DURABILITY].orElse(null) == type.id
    }

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
