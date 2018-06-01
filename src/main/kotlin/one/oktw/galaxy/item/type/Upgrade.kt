package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUpgrade
import one.oktw.galaxy.internal.LanguageService
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ItemType.UPGRADE
import one.oktw.galaxy.item.enums.UpgradeType
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

@BsonDiscriminator
data class Upgrade(
    val type: UpgradeType = UpgradeType.DUMMY,
    var level: Int = 0,
    override val itemType: ItemType = UPGRADE
) : Item {
    override fun createItemStack(): ItemStack {
        //Todo check player lang
        val lang = LanguageService()
        val name = lang.getString("item.Upgrade.${type.name}")
        val color = when (type) {
        // TODO more color
            UpgradeType.RANGE -> TextColors.GREEN
            else -> TextColors.NONE
        }

        return ItemStack.builder()
            .itemType(ItemTypes.ENCHANTED_BOOK)
            .itemData(DataType(UPGRADE))
            .itemData(DataUpgrade(type, level))
            .add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, color, "${lang.getString("item.Upgrade.Item")} Lv.$level".format(name)))
            .build()
    }
}
