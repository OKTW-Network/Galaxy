package one.oktw.galaxy.block

import one.oktw.galaxy.item.enums.ItemType.BLOCK
import one.oktw.galaxy.item.type.Item
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack

data class FakeBlockItem(private val block: FakeBlocks) : Item {
    override val itemType = BLOCK

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(WOODEN_SWORD)
        .add(Keys.UNBREAKABLE, true)
        .add(Keys.ITEM_DURABILITY, block.id)
        .build()
}