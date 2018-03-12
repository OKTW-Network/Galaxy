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
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.cause.First
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
        val task = taskManager.armor

        if (item[DataType.key].orElse(null) != ARMOR) return

        event.cursorTransaction.apply {
            setCustom(ItemStackSnapshot.NONE)
            isValid = true
        }

        when (item.type) {
            DIAMOND_HELMET -> {
                if (task.nightVision.remove(player.uniqueId)) {
                    player.transform(Keys.POTION_EFFECTS) { it.apply { removeIf { it.type == PotionEffectTypes.NIGHT_VISION } } }
                } else {
                    task.nightVision += player.uniqueId
                }

                player.setHelmet(switch(item))
            }
            DIAMOND_CHESTPLATE -> event.isCancelled = true
            DIAMOND_LEGGINGS -> {
                if (task.jump.remove(player.uniqueId) == null) {
                    task.jump[player.uniqueId] = armor.first { it.type == FLEXIBLE }.level - 1
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