package one.oktw.galaxy.item.type

import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUpgrade
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
        val lang = Main.translationService
        val name = lang.of("item.Upgrade.${type.name}")
        val color = when (type) {
        // TODO more color
            RANGE -> TextColors.GREEN
            else -> TextColors.NONE
        }

        return ItemStack.builder()
            .itemType(ENCHANTED_BOOK)
            .itemData(DataItemType(UPGRADE))
            .itemData(DataUpgrade(type, level))
            .add(DISPLAY_NAME, lang.ofPlaceHolder(BOLD, color, name, " Lv.$level".format(name)))
            .build()
    }

    override fun test(item: ItemStack): Boolean {
        return item[DataItemType.key].orElse(null) == UPGRADE &&
                item[DataUpgrade::class.java].orElse(null).let { it?.type == type && it.level == level }
    }

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
