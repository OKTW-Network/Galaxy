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
import net.minecraft.text.Text
import one.oktw.galaxy.item.type.WeaponType

data class SwordData(
    var damage: Double
) : WeaponData(WeaponType.SWORD) {
    companion object {
        val default = SwordData(1.0)
    }

    override fun toLoreText() = arrayListOf(
        loreText(Text.of("傷害"), damage.toString())
    )

    override fun readFromNbt(nbt: NbtCompound) {
        damage = nbt.getDouble("damage")
    }

}
