package one.oktw.galaxy.block.data

import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType.BLOCK
import one.oktw.galaxy.item.type.Item
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.BLUE
import org.spongepowered.api.text.format.TextStyles.BOLD

data class FakeBlockItem(private val block: CustomBlocks) : Item {
    override val itemType = BLOCK

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(WOODEN_SWORD)
        .itemData(DataItemType(BLOCK))
        .itemData(DataBlockType(block))
        .add(DISPLAY_NAME, Text.of(BOLD, BLUE, languageService.getDefaultLanguage()["block.${block.name}"]))
        .add(UNBREAKABLE, true)
        .add(HIDE_UNBREAKABLE, true)
        .add(HIDE_MISCELLANEOUS, true)
        .add(HIDE_ATTRIBUTES, true)
        .add(HIDE_ENCHANTMENTS, true)
        .add(ITEM_DURABILITY, block.id!!)
        .build()
        .let(::removeDamage)
        .let(::removeCoolDown)

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())

    override fun test(itemStack: ItemStack): Boolean {
        return itemStack[DataItemType.key].orElse(null) == BLOCK && itemStack[ITEM_DURABILITY].orElse(null) == block.id
    }
}
