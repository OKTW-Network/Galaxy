package one.oktw.galaxy.armor

import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.ItemType.ARMOR
import one.oktw.galaxy.enums.UpgradeType.*
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffectTypes.*
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentTypes.AQUA_AFFINITY
import org.spongepowered.api.item.enchantment.EnchantmentTypes.FIRE_PROTECTION
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.Arrays.asList

class ArmorHelper {
    companion object {
        fun offerArmor(player: Player) {
            val upgrade = travelerManager.getTraveler(player).armor

            val helmet: ItemStack = getArmor(ItemTypes.DIAMOND_HELMET)
            val chestplate: ItemStack =
                getArmor(ItemTypes.DIAMOND_CHESTPLATE)
            val leggings: ItemStack = getArmor(ItemTypes.DIAMOND_LEGGINGS)
            val boots: ItemStack = getArmor(ItemTypes.DIAMOND_BOOTS)

            upgrade.firstOrNull { it.type == NIGHT_VISION }?.apply {
                helmet.apply {
                    offer(DataEnable())
                    offer(Keys.ITEM_LORE, asList(Text.of(TextColors.RED, TextStyles.UNDERLINE, "夜視鏡").toText()))
                }
            }

            upgrade.firstOrNull { it.type == SHIELD }?.apply {
                chestplate.apply {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    (this as net.minecraft.item.ItemStack).addAttributeModifier(
                        SharedMonsterAttributes.ARMOR.name,
                        AttributeModifier("Armor modifier", level.toDouble() * 5, 0),
                        null
                    )
                }

                if (level == 5) ArmorEffect.offerEffect(player, RESISTANCE, 2)
            }

            upgrade.firstOrNull { it.type == ADAPT }?.apply {
                if (level < 3) helmet.offer(
                    Keys.ITEM_ENCHANTMENTS,
                    asList(Enchantment.of(AQUA_AFFINITY, level))
                ) else {
                    ArmorEffect.offerEffect(player, WATER_BREATHING)
                }

                if (level in 2..3) chestplate.offer(
                    Keys.ITEM_ENCHANTMENTS,
                    asList(Enchantment.of(FIRE_PROTECTION, level))
                ) else if (level > 3) {
                    ArmorEffect.offerEffect(player, FIRE_RESISTANCE)
                }

                leggings.apply {
                    offer(DataEnable())
                    offer(Keys.ITEM_LORE, asList(Text.of(TextColors.RED, TextStyles.UNDERLINE, "跳躍增強").toText()))
                }
                boots.apply {
                    offer(DataEnable())
                    offer(Keys.ITEM_LORE, asList(Text.of(TextColors.RED, TextStyles.UNDERLINE, "速度增強").toText()))
                }
            }

            upgrade.firstOrNull { it.type == FLY }?.apply { player.offer(Keys.CAN_FLY, true) }

            player.setHelmet(helmet)
            player.setChestplate(chestplate)
            player.setLeggings(leggings)
            player.setBoots(boots)
        }

        private fun getArmor(itemType: ItemType): ItemStack {
            val item = ItemStack.builder()
                .itemType(itemType)
                .itemData(DataType.Immutable(ARMOR))
                .itemData(DataUUID.Immutable())
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, "科技裝甲"))
                .add(Keys.UNBREAKABLE, true)
                .add(Keys.HIDE_UNBREAKABLE, true)
                .add(Keys.HIDE_MISCELLANEOUS, true)
                .add(Keys.HIDE_ATTRIBUTES, true)
                .add(Keys.HIDE_ENCHANTMENTS, true)
                .build()

            @Suppress("CAST_NEVER_SUCCEEDS")
            (item as net.minecraft.item.ItemStack).addAttributeModifier(
                SharedMonsterAttributes.ARMOR.name,
                AttributeModifier("Armor modifier", 0.0, 0),
                null
            )

            return item
        }
    }
}