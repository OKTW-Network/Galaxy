package one.oktw.galaxy.helper

import one.oktw.galaxy.data.DataScope
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.types.item.Gun
import one.oktw.galaxy.types.item.ItemBase
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes.IRON_SWORD
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class ItemHelper {
    companion object {
        fun getItem(item: ItemBase): Optional<ItemStack> {
            return when (item) {
                is Gun -> Optional.of(getGun(item))
                else -> Optional.empty()
            }
        }

        private fun getGun(gun: Gun): ItemStack {
            val item = ItemStack.builder()
                    .itemType(gun.type.item)
                    .itemData(DataUUID.Immutable(gun.uuid))
                    .add(Keys.UNBREAKABLE, true)
                    .add(Keys.HIDE_UNBREAKABLE, true)
                    .add(Keys.HIDE_MISCELLANEOUS, true)
                    .add(Keys.HIDE_ATTRIBUTES, true)
                    .add(Keys.HIDE_ENCHANTMENTS, true)
                    .add(Keys.ITEM_DURABILITY, gun.type.id.toInt())

            when (gun.type.item) {
                WOODEN_SWORD -> item.add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.AQUA, "Laser Gun"))

                IRON_SWORD -> {
                    item.itemData(DataScope(false))
                    item.add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.GOLD, "Sniper"))
                }
            }

            return item.build()
        }
    }
}