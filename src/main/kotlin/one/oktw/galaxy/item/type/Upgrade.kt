package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUpgrade
import one.oktw.galaxy.internal.LanguageService
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ItemType.UPGRADE
import one.oktw.galaxy.item.enums.UpgradeType
import one.oktw.galaxy.item.enums.UpgradeType.BASE
import one.oktw.galaxy.item.enums.UpgradeType.RANGE
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys.DISPLAY_NAME
import org.spongepowered.api.item.ItemTypes.ENCHANTED_BOOK
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles.BOLD

@BsonDiscriminator
data class Upgrade(val type: UpgradeType = BASE, var level: Int = 0) : Item {
    override val itemType: ItemType = UPGRADE

    override fun createItemStack(): ItemStack {
        //Todo check player lang
        val lang = LanguageService()
        val name = lang.getString("item.Upgrade.${type.name}")
        val color = when (type) {
        // TODO more color
            RANGE -> TextColors.GREEN
            else -> TextColors.NONE
        }

        return ItemStack.builder()
            .itemType(ENCHANTED_BOOK)
            .itemData(DataType(UPGRADE))
            .itemData(DataUpgrade(type, level))
            .add(DISPLAY_NAME, Text.of(BOLD, color, "${lang.getString("item.Upgrade.Item")} Lv.$level".format(name)))
            .build()
    }

    override fun test(item: ItemStack): Boolean {
        return item[DataType.key].orElse(null) == UPGRADE &&
                item[DataUpgrade::class.java].orElse(null).let { it?.type == type && it.level == level }
    }

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
