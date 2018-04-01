package one.oktw.galaxy.item.type

import one.oktw.galaxy.enums.ItemType
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.item.inventory.ItemStack

@BsonDiscriminator
interface Item {
    val itemType: ItemType

    fun createItemStack(): ItemStack
}
