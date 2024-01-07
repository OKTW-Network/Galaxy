/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

package one.oktw.galaxy.item.data

import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import one.oktw.galaxy.item.type.WeaponType

abstract class WeaponData(val type: WeaponType) {
    abstract fun toLoreText(): ArrayList<Text>

    abstract fun readFromNbt(nbt: NbtCompound)

    internal fun loreText(key: Text, value: String, unit: String = ""): Text =
        key.copy()
            .setStyle(Style.EMPTY.withColor(Formatting.AQUA))
            .append(Text.literal(": ").styled { it.withColor(Formatting.DARK_GRAY) })
            .append(Text.literal(value).styled { it.withColor(Formatting.GRAY) })
            .append(Text.literal(unit).styled { it.withColor(Formatting.DARK_GRAY) })
            .styled { it.withItalic(false) }
}
