package one.oktw.galaxy.event

import net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED
import net.minecraft.entity.ai.attributes.AttributeModifier
import one.oktw.galaxy.Main.Companion.taskManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.enums.ItemType.ARMOR
import one.oktw.galaxy.enums.UpgradeType.FLEXIBLE
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffectTypes.JUMP_BOOST
import org.spongepowered.api.effect.potion.PotionEffectTypes.NIGHT_VISION
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.ItemTypes.*
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.format.TextColors

class Armor {
    @Listener
    fun onClickInventory(event: ClickInventoryEvent, @First player: Player) {
        val item = event.cursorTransaction.default.createStack()
        val armor = travelerManager.getTraveler(player).armor
        val effect = taskManager.effect

        if (item[DataType.key].orElse(null) != ARMOR) return

        if (item[DataEnable.key].isPresent) {
            event.cursorTransaction.apply {
                setCustom(ItemStackSnapshot.NONE)
                isValid = true
            }
        } else {
            event.isCancelled = true
            return
        }

        when (item.type) {
            DIAMOND_HELMET -> {
                if (item[DataEnable.key].get()) {
                    effect.removeEffect(player, NIGHT_VISION)
                } else {
                    effect.addEffect(player, NIGHT_VISION)
                }

                player.setHelmet(switch(item))
            }
            DIAMOND_CHESTPLATE -> Unit
            DIAMOND_LEGGINGS -> {
                if (item[DataEnable.key].get()) {
                    effect.removeEffect(player, JUMP_BOOST)
                } else {
                    effect.addEffect(
                        player,
                        JUMP_BOOST,
                        armor.first { it.type == FLEXIBLE }.level - 1
                    ) // TODO switch jump level
                }

                player.setLeggings(switch(item))
            }
            DIAMOND_BOOTS -> {
                if (item[DataEnable.key].get()) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val itemStack = (item as net.minecraft.item.ItemStack)
                    val nbt = itemStack.tagCompound?.getTagList("AttributeModifiers", 10)!!

                    if (nbt.tagCount() > 1) nbt.removeTag(1)

                    itemStack.setTagInfo("AttributeModifiers", nbt)
                } else {
                    val speed =
                        MOVEMENT_SPEED.defaultValue * (1 + armor.first { it.type == FLEXIBLE }.level / 10)

                    @Suppress("CAST_NEVER_SUCCEEDS")
                    (item as net.minecraft.item.ItemStack).addAttributeModifier(
                        MOVEMENT_SPEED.name,
                        AttributeModifier("Armor modifier", speed, 0),
                        null
                    )
                }

                player.setBoots(switch(item))
            }
            else -> Unit
        }
    }

    @Listener
    fun onChangeInventory(event: ChangeInventoryEvent) {
        if (event.transactions.any { it.default[DataType.key].orElse(null) == ARMOR }) event.isCancelled = true
    }

    private fun switch(item: ItemStack): ItemStack {
        item.transform(DataEnable.key) {
            if (it) {
                item.transform(Keys.ITEM_LORE) { it[0] = it[0].toBuilder().color(TextColors.RED).build();it }
            } else {
                item.transform(Keys.ITEM_LORE) { it[0] = it[0].toBuilder().color(TextColors.GREEN).build();it }
            }

            !it
        }

        return item
    }
}