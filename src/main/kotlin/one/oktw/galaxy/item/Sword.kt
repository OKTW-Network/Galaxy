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
import one.oktw.galaxy.item.data.SwordData

class Sword(id: String, modelData: Int, name: String) : Weapon(id, modelData, name) {
    companion object {
        val SWORD_KATANA_OFF = registry.register(Sword("sword_katana_off", 3020100, "item.Weapon.KATANA"))
        val SWORD_KATANA_ON = registry.register(Sword("sword_katana_on", 3020101, "item.Weapon.KATANA"))
        val SWORD_KATANA_SCABBARD = registry.register(Sword("sword_katana_scabbard", 3020102, "item.Weapon.KATANA.SCABARD"))
        val SWORD_MAGI_OFF = registry.register(Sword("sword_magi_off", 3020200, "item.Weapon.MAGI"))
        val SWORD_MAGI_ON = registry.register(Sword("sword_magi_on", 3020201, "item.Weapon.MAGI"))
        val SWORD_GHOST_CUTTER_OFF = registry.register(Sword("sword_ghost_cutter_off", 3020300, "item.event.halloween2018.undeadKiller"))
        val SWORD_GHOST_CUTTER_ON = registry.register(Sword("sword_ghost_cutter_on", 3020301, "item.event.halloween2018.undeadKiller"))
        val SWORD_NAZO_OFF = registry.register(Sword("sword_nazo_off", 3020400, "item.Weapon.NAZO"))
        val SWORD_NAZO_ON = registry.register(Sword("sword_nazo_on", 3020401, "item.Weapon.NAZO"))
        val SWORD_NAZO_SCABBARD = registry.register(Sword("sword_nazo_scabbard", 3020402, "item.Weapon.NAZO.SCABARD"))
        val SWORD_RANBO_OFF = registry.register(Sword("sword_ranbo_off", 3020500, "item.Weapon.RANBO"))
        val SWORD_RANBO_ON = registry.register(Sword("sword_ranbo_on", 3020501, "item.Weapon.RANBO"))
        val SWORD_PLASUM_OFF = registry.register(Sword("sword_plasum_off", 3020600, "item.Weapon.PLASUM"))
        val SWORD_PLASUM_ON = registry.register(Sword("sword_plasum_on", 3020601, "item.Weapon.PLASUM"))
        val SWORD_NANOSABER_OFF = registry.register(Sword("sword_nanosaber_off", 3020700, "item.Weapon.NANOSABER"))
        val SWORD_NANOSABER_ON = registry.register(Sword("sword_nanosaber_on", 3020701, "item.Weapon.NANOSABER"))
        val SWORD_NANOSABER_SCABBARD = registry.register(Sword("sword_nanosaber_scabbard", 3020702, "item.Weapon.NANOSABER.SCABARD"))
    }

    override val weaponData = SwordData.default

    override fun readCustomNbt(nbt: NbtCompound): CustomItem {
        val nbtData = nbt.getCompound("WeaponData")
        weaponData.readFromNbt(nbtData)
        return super.readCustomNbt(nbt)
    }
}
