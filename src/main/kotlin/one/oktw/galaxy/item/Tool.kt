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

class Tool private constructor(id: String, private val name: String) :
    CustomItem(Identifier.of("galaxy", "item/tool/$id"), COMMAND_BLOCK, maxStackSize = 1) {
    override val cacheable = false

    companion object {
        val WRENCH = registry.register(Tool("wrench", "item.Tool.WRENCH"))
        val CROWBAR = registry.register(Tool("crowbar", "item.Tool.CROWBAR"))
    }

    override fun getName(): Text = translatable(name).styled { it.withColor(Formatting.WHITE).withItalic(false) }
}
