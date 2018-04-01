package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.enums.ButtonType
import one.oktw.galaxy.enums.ItemType.BUTTON
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack

@BsonDiscriminator
data class Button @BsonCreator constructor(@BsonProperty("type") val type: ButtonType) : Item {
    @BsonProperty("itemType")
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
}
