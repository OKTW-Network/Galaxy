package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack

@BsonDiscriminator
data class Button(val type: ButtonType = ButtonType.BLANK) : Item {
    override val itemType = BUTTON

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(ItemTypes.DIAMOND_HOE)
        .itemData(DataType(BUTTON))
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
