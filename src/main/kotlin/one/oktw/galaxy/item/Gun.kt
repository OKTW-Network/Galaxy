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

package one.oktw.galaxy.item

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.MathHelper
import one.oktw.galaxy.item.data.GunData

class Gun(id: String, modelData: Int, name: String) : Weapon(id, modelData, name) {
    companion object {
        val PISTOL = registry.register(Gun("pistol", 3010100, "item.Gun.PISTOL"))
        val PISTOL_LASOR = registry.register(Gun("pistol_lasor", 3010200, "item.Gun.PISTOL"))
        val PISTOL_LASOR_AIMING = registry.register(Gun("pistol_lasor_aiming", 3010201, "item.Gun.PISTOL"))
        val SNIPER = registry.register(Gun("sniper", 3010300, "item.Gun.SNIPER"))
        val SNIPER_AIMING = registry.register(Gun("sniper_aiming", 3010301, "item.Gun.SNIPER"))
        val RAILGUN = registry.register(Gun("railgun", 3010400, "item.Gun.RAILGUN"))
        val RAILGUN_AIMING = registry.register(Gun("railgun_aiming", 3010401, "item.Gun.RAILGUN"))
    }

    override val weaponData = GunData()

    fun migrateData(item: Gun) {
        val oldData = item.weaponData
        weaponData.applyValue(oldData.heat, oldData.maxTemp, oldData.cooling, oldData.damage, oldData.range, oldData.through)
    }

    override fun readCustomNbt(nbt: NbtCompound): CustomItem {
        val nbtData = nbt.getCompound("WeaponData")
        weaponData.readFromNbt(nbtData)
        return super.readCustomNbt(nbt)
    }

    override fun writeCustomNbt(nbt: NbtCompound) {
        super.writeCustomNbt(nbt)
        nbt.put(
            "WeaponData",
            NbtCompound().apply {
                putUuid("id", MathHelper.randomUuid())
                putInt("heat", weaponData.heat)
                putInt("maxTemp", weaponData.maxTemp)
                putDouble("cooling", weaponData.cooling)
                putDouble("damage", weaponData.damage)
                putDouble("range", weaponData.range)
                putInt("through", weaponData.through)
            }
        )
    }
}
