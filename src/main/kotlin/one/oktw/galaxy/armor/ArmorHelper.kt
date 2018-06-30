package one.oktw.galaxy.armor

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED
import net.minecraft.entity.ai.attributes.AttributeModifier
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.armor.ArmorEffect.Companion.offerEffect
import one.oktw.galaxy.armor.ArmorEffect.Companion.removeEffect
import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.getTraveler
import one.oktw.galaxy.item.enums.ItemType.ARMOR
import one.oktw.galaxy.item.enums.UpgradeType.*
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.effect.potion.PotionEffectTypes.*
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes.*
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
        fun offerArmor(player: Player) = launch {
            val lang = languageService.getDefaultLanguage() // TODO set language
            val upgrade = getTraveler(player).await()?.armor ?: return@launch
            val helmet: ItemStack = getArmor(DIAMOND_HELMET)
            val chestplate: ItemStack = getArmor(DIAMOND_CHESTPLATE)
            val leggings: ItemStack = getArmor(DIAMOND_LEGGINGS)
            val boots: ItemStack = getArmor(DIAMOND_BOOTS)

            upgrade.firstOrNull { it.type == NIGHT_VISION }?.apply {
                helmet.apply {
                    offer(DataEnable())
                    offer(
                        ITEM_LORE,
                        asList(Text.of(TextColors.RED, TextStyles.UNDERLINE, lang["armor.effect.night_vision"]))
                    )
                }
            }

            upgrade.firstOrNull { it.type == SHIELD }?.apply {
                @Suppress("CAST_NEVER_SUCCEEDS")
                (chestplate as net.minecraft.item.ItemStack).addAttributeModifier(
                    SharedMonsterAttributes.ARMOR.name,
                    AttributeModifier("Armor modifier", level.toDouble() * 5, 0),
                    null
                )

                if (level == 5) ArmorEffect.offerEffect(player, RESISTANCE, 2)
            }

            upgrade.firstOrNull { it.type == ADAPT }?.apply {
                if (level < 3) {
                    helmet.offer(ITEM_ENCHANTMENTS, asList(Enchantment.of(AQUA_AFFINITY, level)))
                } else {
                    ArmorEffect.offerEffect(player, WATER_BREATHING)
                }

                if (level in 2..3) {
                    chestplate.offer(ITEM_ENCHANTMENTS, asList(Enchantment.of(FIRE_PROTECTION, level)))
                } else if (level > 3) {
                    ArmorEffect.offerEffect(player, FIRE_RESISTANCE)
                }

                leggings.apply {
                    offer(DataEnable())
                    offer(
                        ITEM_LORE,
                        asList(Text.of(TextColors.RED, TextStyles.UNDERLINE, lang["armor.effect.jump_boost"]))
                    )
                }

                boots.apply {
                    offer(DataEnable())
                    offer(
                        ITEM_LORE,
                        asList(Text.of(TextColors.RED, TextStyles.UNDERLINE, lang["armor.effect.speed_boost"]))
                    )
                }
            }

            withContext(serverThread) {
                upgrade.firstOrNull { it.type == FLY }?.apply { player.offer(CAN_FLY, true) }

                player.setHelmet(helmet)
                player.setChestplate(chestplate)
                player.setLeggings(leggings)
                player.setBoots(boots)
            }
        }

        fun toggleHelmet(player: Player) {
            val item = player.helmet.get()

            if (item[DataEnable.key].get()) {
                removeEffect(player, NIGHT_VISION)
            } else {
                offerEffect(player, NIGHT_VISION)
            }

            player.setHelmet(toggleArmorStatus(item))
        }

        fun toggleChestplate(player: Player) {
            // TODO
        }

        fun toggleLeggings(player: Player) = launch(serverThread) {
            val item = player.leggings.get()
            val armor = getTraveler(player).await()?.armor ?: return@launch

            if (item[DataEnable.key].get()) {
                removeEffect(player, JUMP_BOOST)
            } else {
                offerEffect(player, JUMP_BOOST, armor.first { it.type == FLEXIBLE }.level - 1)
            }

            player.setLeggings(toggleArmorStatus(item))
        }

        fun toggleBoots(player: Player) = launch(serverThread) {
            val item = player.boots.get()
            val armor = getTraveler(player).await()?.armor ?: return@launch

            if (item[DataEnable.key].get()) {
                @Suppress("CAST_NEVER_SUCCEEDS")
                val itemStack = (item as net.minecraft.item.ItemStack)
                val nbt = itemStack.tagCompound?.getTagList("AttributeModifiers", 10)!!

                if (nbt.tagCount() > 1) nbt.removeTag(1)

                itemStack.setTagInfo("AttributeModifiers", nbt)
            } else {
                val speed = MOVEMENT_SPEED.defaultValue * (1 + armor.first { it.type == FLEXIBLE }.level / 10)

                @Suppress("CAST_NEVER_SUCCEEDS")
                (item as net.minecraft.item.ItemStack).addAttributeModifier(
                    MOVEMENT_SPEED.name,
                    AttributeModifier("Armor modifier", speed, 0),
                    null
                )
            }

            player.setBoots(toggleArmorStatus(item))
        }

        private fun getArmor(itemType: ItemType): ItemStack {
            val item = ItemStack.builder()
                .itemType(itemType)
                .itemData(DataItemType.Immutable(ARMOR))
                .itemData(DataUUID.Immutable())
                .add(
                    DISPLAY_NAME,
                    Text.of(TextColors.YELLOW, TextStyles.BOLD, languageService.getDefaultLanguage()["armor.item.name"])
                )
                .add(UNBREAKABLE, true)
                .add(HIDE_UNBREAKABLE, true)
                .add(HIDE_MISCELLANEOUS, true)
                .add(HIDE_ATTRIBUTES, true)
                .add(HIDE_ENCHANTMENTS, true)
                .build()

            @Suppress("CAST_NEVER_SUCCEEDS")
            (item as net.minecraft.item.ItemStack).addAttributeModifier(
                SharedMonsterAttributes.ARMOR.name,
                AttributeModifier("Armor modifier", 0.0, 0),
                null
            )

            return item
        }

        private fun toggleArmorStatus(item: ItemStack): ItemStack {
            item.transform(DataEnable.key) {
                if (it) {
                    item.transform(ITEM_LORE) { it[0] = it[0].toBuilder().color(TextColors.RED).build();it }
                } else {
                    item.transform(ITEM_LORE) { it[0] = it[0].toBuilder().color(TextColors.GREEN).build();it }
                }

                !it
            }

            return item
        }
    }
}
