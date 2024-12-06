/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2024
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

import net.minecraft.item.Items.COMMAND_BLOCK
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class Weapon private constructor(id: String, private val name: String) :
    CustomItem(Identifier.of("galaxy", "item/weapon/$id"), COMMAND_BLOCK, maxStackSize = 1) {
    override val cacheable = false

    companion object {
        val PISTOL = registry.register(Weapon("pistol", "item.Gun.PISTOL"))
        val PISTOL_LASOR = registry.register(Weapon("pistol_lasor", "item.Gun.PISTOL"))
        val PISTOL_LASOR_AIMING = registry.register(Weapon("pistol_lasor_aiming", "item.Gun.PISTOL"))
        val SNIPER = registry.register(Weapon("sniper", "item.Gun.SNIPER"))
        val SNIPER_AIMING = registry.register(Weapon("sniper_aiming", "item.Gun.SNIPER"))
        val RAILGUN = registry.register(Weapon("railgun", "item.Gun.RAILGUN"))
        val RAILGUN_AIMING = registry.register(Weapon("railgun_aiming", "item.Gun.RAILGUN"))
        val SWORD_KATANA_OFF = registry.register(Weapon("sword_katana_off", "item.Weapon.KATANA"))
        val SWORD_KATANA_ON = registry.register(Weapon("sword_katana_on", "item.Weapon.KATANA"))
        val SWORD_KATANA_SCABBARD = registry.register(Weapon("sword_katana_scabbard", "item.Weapon.KATANA.SCABARD"))
        val SWORD_MAGI_OFF = registry.register(Weapon("sword_magi_off", "item.Weapon.MAGI"))
        val SWORD_MAGI_ON = registry.register(Weapon("sword_magi_on", "item.Weapon.MAGI"))
        val SWORD_GHOST_CUTTER_OFF = registry.register(Weapon("sword_ghost_cutter_off", "item.event.halloween2018.undeadKiller"))
        val SWORD_GHOST_CUTTER_ON = registry.register(Weapon("sword_ghost_cutter_on", "item.event.halloween2018.undeadKiller"))
        val SWORD_NAZO_OFF = registry.register(Weapon("sword_nazo_off", "item.Weapon.NAZO"))
        val SWORD_NAZO_ON = registry.register(Weapon("sword_nazo_on", "item.Weapon.NAZO"))
        val SWORD_NAZO_SCABBARD = registry.register(Weapon("sword_nazo_scabbard", "item.Weapon.NAZO.SCABARD"))
        val SWORD_RANBO_OFF = registry.register(Weapon("sword_ranbo_off", "item.Weapon.RANBO"))
        val SWORD_RANBO_ON = registry.register(Weapon("sword_ranbo_on", "item.Weapon.RANBO"))
        val SWORD_PLASUM_OFF = registry.register(Weapon("sword_plasum_off", "item.Weapon.PLASUM"))
        val SWORD_PLASUM_ON = registry.register(Weapon("sword_plasum_on", "item.Weapon.PLASUM"))
        val SWORD_NANOSABER_OFF = registry.register(Weapon("sword_nanosaber_off", "item.Weapon.NANOSABER"))
        val SWORD_NANOSABER_ON = registry.register(Weapon("sword_nanosaber_on", "item.Weapon.NANOSABER"))
        val SWORD_NANOSABER_SCABBARD = registry.register(Weapon("sword_nanosaber_scabbard", "item.Weapon.NANOSABER.SCABARD"))
    }

    override fun getName(): Text = translatable(name).styled { it.withColor(Formatting.WHITE).withItalic(false) }
}
