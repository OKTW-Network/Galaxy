package one.oktw.galaxy.helper

import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.enums.ItemType.ARMOR
import one.oktw.galaxy.enums.UpgradeType.*
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentTypes.AQUA_AFFINITY
import org.spongepowered.api.item.enchantment.EnchantmentTypes.FIRE_PROTECTION
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.Arrays.asList

class ArmorHelper {
    companion object {
        fun offerArmor(player: Player) {
            val upgrade = travelerManager.getTraveler(player).armor

            val helmet: ItemStack = itemHelper(ItemTypes.DIAMOND_HELMET)
            val chestplate: ItemStack = itemHelper(ItemTypes.DIAMOND_CHESTPLATE)
            val leggings: ItemStack = itemHelper(ItemTypes.DIAMOND_LEGGINGS)
            val boots: ItemStack = itemHelper(ItemTypes.DIAMOND_BOOTS)

            upgrade.firstOrNull { it.type == NIGHT_VISION }?.apply {
                helmet.apply {
                    offer(DataEnable())
                    offer(Keys.ITEM_LORE, asList(Text.of(TextColors.RED, "點擊切換夜視").toText()))
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
            }

            upgrade.firstOrNull { it.type == ADAPT }?.apply {
                if (level < 3) helmet.offer(
                    Keys.ITEM_ENCHANTMENTS,
                    asList(Enchantment.of(AQUA_AFFINITY, level))
                )

                if (level in 2..3) chestplate.offer(
                    Keys.ITEM_ENCHANTMENTS,
                    asList(Enchantment.of(FIRE_PROTECTION, level))
                )

                leggings.apply {
                    offer(DataEnable())
                    offer(Keys.ITEM_LORE, asList(Text.of(TextColors.RED, "點擊切換跳躍").toText()))
                }
                boots.apply {
                    offer(DataEnable())
                    offer(Keys.ITEM_LORE, asList(Text.of(TextColors.RED, "點擊切換速度").toText()))
                }
            }

            upgrade.firstOrNull { it.type == FLY }?.apply { player.offer(Keys.CAN_FLY, true) }

            player.setHelmet(helmet)
            player.setChestplate(chestplate)
            player.setLeggings(leggings)
            player.setBoots(boots)
        }

        private fun itemHelper(itemType: ItemType): ItemStack {
            val item = ItemStack.builder()
                .itemType(itemType)
                .itemData(DataType.Immutable(ARMOR))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "科技裝甲"))
//                .add(Keys.ITEM_ENCHANTMENTS, asList(Enchantment.of(BINDING_CURSE, 1))) // need testing
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