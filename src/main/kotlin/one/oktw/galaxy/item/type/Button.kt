package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ButtonType.BLANK
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes.DIAMOND_HOE
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text

@BsonDiscriminator
data class Button(val type: ButtonType = BLANK) : Item {
    override val itemType = BUTTON

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(DIAMOND_HOE)
        .itemData(DataItemType(BUTTON))
        .add(DISPLAY_NAME, Text.EMPTY)
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
        return item[DataItemType.key].orElse(null) == type && item[ITEM_DURABILITY].orElse(null) == type.id
    }

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
