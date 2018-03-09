package one.oktw.galaxy.helper

import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.UpgradeType.*
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentTypes
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
                helmet.apply { offer(Keys.ITEM_LORE, asList(Text.of(TextColors.YELLOW, "點擊切換夜視").toText())) }
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
                leggings.apply { offer(Keys.ITEM_LORE, asList(Text.of(TextColors.YELLOW, "點擊切換跳躍").toText())) }
                boots.apply { offer(Keys.ITEM_LORE, asList(Text.of(TextColors.YELLOW, "點擊切換速度").toText())) }
            }

            player.setHelmet(helmet)
            player.setChestplate(chestplate)
            player.setLeggings(leggings)
            player.setBoots(boots)
        }

        private fun itemHelper(itemType: ItemType): ItemStack {
            val item = ItemStack.builder()
                .itemType(itemType)
                .itemData(DataUUID().asImmutable())
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "科技裝甲"))
                .add(Keys.ITEM_ENCHANTMENTS, asList(Enchantment.of(EnchantmentTypes.BINDING_CURSE, 1)))
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