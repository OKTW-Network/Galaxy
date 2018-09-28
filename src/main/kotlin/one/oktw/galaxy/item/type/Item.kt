package one.oktw.galaxy.item.type

import one.oktw.galaxy.item.enums.ItemType
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.recipe.crafting.Ingredient

@BsonDiscriminator
interface Item : Ingredient {
    val itemType: ItemType

    fun createItemStack(): ItemStack
}
