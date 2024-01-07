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

import net.minecraft.item.ItemStack
import net.minecraft.item.Items.COMMAND_BLOCK
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import one.oktw.galaxy.item.data.WeaponData
import one.oktw.galaxy.util.LoreEditor.Companion.loreEditor

abstract class Weapon(id: String, modelData: Int, private val name: String) :
    CustomItem(Identifier("galaxy", "item/weapon/$id"), COMMAND_BLOCK, modelData) {
    override val cacheable = false

    abstract val weaponData: WeaponData

    override fun getName(): Text = translatable(name).styled { it.withColor(Formatting.WHITE).withItalic(false) }

    override fun createItemStack(): ItemStack = super.createItemStack().apply {
        loreEditor {
            addText(weaponData.toLoreText())
        }
    }
}
