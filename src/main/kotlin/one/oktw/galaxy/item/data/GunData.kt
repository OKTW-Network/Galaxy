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

data class GunData(
    var heat: Int,
    var maxTemp: Int,
    var cooling: Double,
    var damage: Double,
    var range: Double,
    var through: Int
) : WeaponData(WeaponType.GUN) {
    companion object {
        val default = GunData(1, 1, 1.0, 1.0, 1.0, 1)
    }

    override fun toLoreText() = arrayListOf(
        loreText(Text.of("傷害"), damage.toString()),
        loreText(Text.of("射程"), range.toString(), "B"),
        loreText(Text.of("穿透"), through.toString(), ""),
        loreText(Text.of("積熱"), heat.toString(), "K/shot"),
        loreText(Text.of("耐熱"), maxTemp.toString(), "K"),
        loreText(Text.of("冷卻"), cooling.toString(), "K/t")
    )

    override fun readFromNbt(nbt: NbtCompound) = applyValue(
        nbt.getInt("heat"),
        nbt.getInt("maxTemp"),
        nbt.getDouble("cooling"),
        nbt.getDouble("damage"),
        nbt.getDouble("range"),
        nbt.getInt("through")
    )

    fun applyValue(heat: Int, maxTemp: Int, cooling: Double, damage: Double, range: Double, through: Int) {
        this.heat = heat
        this.maxTemp = maxTemp
        this.cooling = cooling
        this.damage = damage
        this.range = range
        this.through = through
    }

}
