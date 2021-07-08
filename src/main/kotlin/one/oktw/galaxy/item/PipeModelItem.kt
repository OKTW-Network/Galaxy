/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class PipeModelItem private constructor(id: String, modelData: Int, private val name: String) :
    CustomItem(Identifier("galaxy", "item/pipe/$id"), Items.COMMAND_BLOCK, modelData) {
    companion object {
        val PIPE_EXTENDED = registry.register(PipeModelItem("pipe_extended", 1010501, "pipe.extended"))
        val PIPE_PORT_EXPORT = registry.register(PipeModelItem("pipe_port_export", 1010502, "pipe.export"))
        val PIPE_PORT_IMPORT = registry.register(PipeModelItem("pipe_port_import", 1010503, "pipe.import"))
        val PIPE_PORT_STORAGE = registry.register(PipeModelItem("pipe_port_storage", 1010504, "pipe.storage"))
    }

    override fun getName(): Text? = name.let(::TranslatableText).styled { it.withColor(Formatting.WHITE).withItalic(false) }
}
