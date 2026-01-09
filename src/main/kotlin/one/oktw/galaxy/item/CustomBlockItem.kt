/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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

import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Items.COMMAND_BLOCK
import one.oktw.galaxy.block.CustomBlock

class CustomBlockItem private constructor(private val id: String, private val name: String?) :
    CustomItem(Identifier.fromNamespaceAndPath("galaxy", "block/$id"), COMMAND_BLOCK) {
    companion object {
        val HT_CRAFTING_TABLE = registry.register(CustomBlockItem("ht_crafting_table", "block.HT_CRAFTING_TABLE"))
        val ELEVATOR = registry.register(CustomBlockItem("elevator", "block.ELEVATOR"))
        val ANGEL_BLOCK = registry.register(CustomBlockItem("angel_block", "block.ANGEL_BLOCK"))
        val TRASHCAN = registry.register(CustomBlockItem("trashcan", "block.TRASHCAN"))
        val TELEPORTER_CORE_BASIC = registry.register(CustomBlockItem("teleporter_core_basic", "block.TELEPORTER"))
        val TELEPORTER_CORE_ADVANCE = registry.register(CustomBlockItem("teleporter_core_advance", "block.TELEPORTER_ADVANCED"))
        val TELEPORTER_FRAME = registry.register(CustomBlockItem("teleporter_frame", "block.TELEPORTER_FRAME"))
        val TEST_GUI = registry.register(CustomBlockItem("test_gui", "block.TEST_GUI"))
        val HARVEST = registry.register(CustomBlockItem("harvest", "block.HARVEST"))
    }

    fun getBlock(): CustomBlock {
        return CustomBlock.registry.get(Identifier.fromNamespaceAndPath("galaxy", "block/$id"))!!
    }

    override fun getName(): Component? = name?.let(Component::translatable)
}
