package one.oktw.galaxy.block

import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.item.enums.ItemType.BLOCK
import one.oktw.galaxy.item.type.Item
import org.spongepowered.api.data.key.Keys.ITEM_DURABILITY
import org.spongepowered.api.data.key.Keys.UNBREAKABLE
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack

data class FakeBlockItem(private val block: FakeBlocks) : Item {
    override val itemType = BLOCK

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(WOODEN_SWORD)
        .itemData(DataType(BLOCK))
        .itemData(DataBlockType(block))
        .add(UNBREAKABLE, true)
        .add(ITEM_DURABILITY, block.id)
        .build()

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())

    override fun test(itemStack: ItemStack): Boolean {
        return itemStack[DataType.key].orElse(null) == BLOCK && itemStack[ITEM_DURABILITY].orElse(null) == block.id
    }
}
