/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

class ItemUtil {
    companion object {
        fun setAttributes(tag: CompoundTag): CompoundTag {
            // give unbreakable
            tag.putBoolean("Unbreakable", true)
            // hide all flag
            tag.putInt("HideFlags", 63)
            return tag
        }

        fun removeAllModifiers(tag: CompoundTag): CompoundTag {
            // remove all modifiers(attack damage, attack speed)
            tag.put("AttributeModifiers", ListTag())
            return tag
        }
    }
}
