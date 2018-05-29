package one.oktw.galaxy.item

import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import org.spongepowered.api.item.inventory.ItemStack

class ItemUtil {
    companion object {
        fun removeCoolDown(itemStack: ItemStack): ItemStack {
            return itemStack.also {
                @Suppress("CAST_NEVER_SUCCEEDS")
                (it as net.minecraft.item.ItemStack).addAttributeModifier(
                    SharedMonsterAttributes.ATTACK_SPEED.name,
                    AttributeModifier("Weapon modifier", 0.0, 0),
                    null
                )
            }
        }

        fun removeDamage(itemStack: ItemStack): ItemStack {
            return itemStack.also {
                @Suppress("CAST_NEVER_SUCCEEDS")
                (it as net.minecraft.item.ItemStack).addAttributeModifier(
                    SharedMonsterAttributes.ATTACK_DAMAGE.name,
                    AttributeModifier("Weapon modifier", 0.0, 0),
                    null
                )
            }
        }
    }
}
