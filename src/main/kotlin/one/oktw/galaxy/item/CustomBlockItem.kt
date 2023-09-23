/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import one.oktw.galaxy.block.CustomBlock

class CustomBlockItem private constructor(private val id: String, modelData: Int, private val name: String?) :
    CustomItem(Identifier("galaxy", "item/block/$id"), COMMAND_BLOCK, modelData) {
    companion object {
        val HT_CRAFTING_TABLE = registry.register(CustomBlockItem("ht_crafting_table", 1010100, "block.HT_CRAFTING_TABLE"))
        val ELEVATOR = registry.register(CustomBlockItem("elevator", 1010200, "block.ELEVATOR"))
        val ANGEL_BLOCK = registry.register(CustomBlockItem("angel_block", 1010400, "block.ANGEL_BLOCK"))
        val TRASHCAN = registry.register(CustomBlockItem("trashcan", 1010500, "block.TRASHCAN"))
        val TELEPORTER_CORE_BASIC = registry.register(CustomBlockItem("teleporter_core_basic", 1010300, "block.TELEPORTER"))
        val TELEPORTER_CORE_ADVANCE = registry.register(CustomBlockItem("teleporter_core_advance", 1010301, "block.TELEPORTER_ADVANCED"))
        val TELEPORTER_FRAME = registry.register(CustomBlockItem("teleporter_frame", 1010302, "block.TELEPORTER_FRAME"))
        val TEST_GUI = registry.register(CustomBlockItem("test_gui", 9999999, "block.TEST_GUI"))
        val HARVEST = registry.register(CustomBlockItem("harvest", 1010700, "block.HARVEST"))
    }

    fun getBlock(): CustomBlock {
        return CustomBlock.registry.get(Identifier("galaxy", "block/$id"))!!
    }

    override fun getName(): Text? = name?.let(Text::translatable)?.styled { it.withColor(Formatting.WHITE).withItalic(false) }
}
