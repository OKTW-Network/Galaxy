package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.internal.LangSys
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType.TOOL
import one.oktw.galaxy.item.enums.ToolType
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes.IRON_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

@BsonDiscriminator
class Tool(val type: ToolType = ToolType.DUMMY) : Item {
    //Todo check player lang
    val lang = LangSys().rootNode.getNode("item","Tool")
    override val itemType = TOOL

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(IRON_SWORD)
        .itemData(DataType(TOOL))
        .add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.YELLOW, lang.getNode(type.name).string))
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
