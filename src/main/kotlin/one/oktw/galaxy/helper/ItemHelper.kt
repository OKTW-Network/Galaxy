package one.oktw.galaxy.helper

import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import one.oktw.galaxy.data.*
import one.oktw.galaxy.enums.ItemType.*
import one.oktw.galaxy.enums.UpgradeType
import one.oktw.galaxy.types.item.Button
import one.oktw.galaxy.types.item.Gun
import one.oktw.galaxy.types.item.Item
import one.oktw.galaxy.types.item.Upgrade
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.ItemTypes.DIAMOND_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

class ItemHelper {
    companion object {
        fun getItem(item: Item): ItemStack? {
            return when (item) {
                is Gun -> removeCoolDown(getGun(item))
                is Upgrade -> getItemUpgrade(item)
                is Button -> getButton(item)
                else -> null
            }
        }

        private fun removeCoolDown(itemStack: ItemStack): ItemStack {
            @Suppress("CAST_NEVER_SUCCEEDS")
            (itemStack as net.minecraft.item.ItemStack).addAttributeModifier(
                SharedMonsterAttributes.ATTACK_SPEED.name,
                AttributeModifier("Weapon modifier", 0.0, 0),
                null
            )

            return itemStack
        }

        private fun getGun(gun: Gun): ItemStack {
            val item = ItemStack.builder()
                .itemType(DIAMOND_SWORD)
                .itemData(DataUUID.Immutable(gun.uuid))
                .itemData(DataOverheat())
                .add(Keys.UNBREAKABLE, true)
                .add(Keys.HIDE_UNBREAKABLE, true)
                .add(Keys.HIDE_MISCELLANEOUS, true)
                .add(Keys.HIDE_ATTRIBUTES, true)
                .add(Keys.HIDE_ENCHANTMENTS, true)
                .add(Keys.ITEM_DURABILITY, gun.style.id.toInt())

            when (gun.itemType) {
                PISTOL -> item.add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.AQUA, "Laser Gun"))
                SNIPER -> {
                    item.itemData(DataEnable())
                    item.add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.GOLD, "Sniper"))
                }
                else -> Unit
            }

            val itemStack = item.build()

            @Suppress("CAST_NEVER_SUCCEEDS")
            (itemStack as net.minecraft.item.ItemStack).addAttributeModifier(
                SharedMonsterAttributes.ATTACK_DAMAGE.name,
                AttributeModifier("Weapon modifier", 1.0, 0),
                null
            )

            return itemStack
        }

        private fun getItemUpgrade(upgrade: Upgrade): ItemStack {
            val name = upgrade.type.name.substring(0, 1) + upgrade.type.name.substring(1).toLowerCase()
            val color = when (upgrade.type) {
                UpgradeType.RANGE -> TextColors.GREEN
                else -> TextColors.NONE
            }

            return ItemStack.builder()
                .itemType(ItemTypes.ENCHANTED_BOOK)
                .itemData(DataUpgrade(upgrade.type, upgrade.level))
                .add(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, color, "$name Upgrade Lv.${upgrade.level}"))
                .build()
        }

        private fun getButton(button: Button): ItemStack {
            return ItemStack.builder()
                .itemType(ItemTypes.DIAMOND_HOE)
                .itemData(DataType(BUTTON))
                .add(Keys.UNBREAKABLE, true)
                .add(Keys.HIDE_UNBREAKABLE, true)
                .add(Keys.HIDE_MISCELLANEOUS, true)
                .add(Keys.HIDE_ATTRIBUTES, true)
                .add(Keys.HIDE_ENCHANTMENTS, true)
                .add(Keys.ITEM_DURABILITY, button.type.id)
                .build()
        }
    }
}
