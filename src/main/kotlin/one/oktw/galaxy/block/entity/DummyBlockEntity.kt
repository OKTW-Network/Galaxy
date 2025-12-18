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

package one.oktw.galaxy.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import one.oktw.galaxy.block.CustomBlock

class DummyBlockEntity(type: BlockEntityType<*>, pos: BlockPos) : CustomBlockEntity(type, pos) {
    override fun loadAdditional(view: ValueInput) {
        super.loadAdditional(view)
        view.getStringOr("id", "").let(Identifier::tryParse)?.let(CustomBlock.registry::get)?.let {
            if (it != CustomBlock.DUMMY) {
                level?.removeBlockEntity(worldPosition)
                level?.setBlockEntity(it.createBlockEntity(worldPosition).apply { readCopyableData(view) })
            }
        }
    }

    override fun saveAdditional(view: ValueOutput) {
        // I'm dummy.
    }
}
