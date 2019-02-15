/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
