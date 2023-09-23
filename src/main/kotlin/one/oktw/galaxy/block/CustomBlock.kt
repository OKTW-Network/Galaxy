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

package one.oktw.galaxy.block

import net.minecraft.block.Block
import net.minecraft.block.Blocks.*
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.BlockEntityType.Builder
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.util.CustomRegistry
import one.oktw.galaxy.util.Registrable

open class CustomBlock(final override val identifier: Identifier, val baseBlock: Block = BARRIER) : Registrable {
    constructor(id: String, baseBlock: Block = BARRIER) : this(Identifier("galaxy", "block/$id"), baseBlock)

    protected val blockEntityType: BlockEntityType<CustomBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        identifier,
        Builder.create({ pos, _ -> createBlockEntity(pos) }, BARRIER).build(null)
    )

    companion object {
        val registry = CustomRegistry<CustomBlock>()

        val DUMMY = registry.register(DummyBlock())
        val CONTROL_PANEL = registry.register(CustomBlock("control_panel", baseBlock = COMPARATOR))
        val PLANET_TERMINAL = registry.register(CustomBlock("planet_terminal", baseBlock = BEACON))
        val HT_CRAFTING_TABLE = registry.register(ModelCustomBlock("ht_crafting_table", CustomBlockItem.HT_CRAFTING_TABLE.createItemStack()))
        val ELEVATOR = registry.register(ModelCustomBlock("elevator", CustomBlockItem.ELEVATOR.createItemStack()))
        val ANGEL_BLOCK = registry.register(ModelCustomBlock("angel_block", CustomBlockItem.ANGEL_BLOCK.createItemStack()))
        val TRASHCAN = registry.register(TrashcanBlock("trashcan", CustomBlockItem.TRASHCAN.createItemStack()))
        val TELEPORTER_CORE_BASIC = registry.register(ModelCustomBlock("teleporter_core_basic", CustomBlockItem.TELEPORTER_CORE_BASIC.createItemStack()))
        val TELEPORTER_CORE_ADVANCE = registry.register(ModelCustomBlock("teleporter_core_advance", CustomBlockItem.TELEPORTER_CORE_ADVANCE.createItemStack()))
        val TELEPORTER_FRAME = registry.register(ModelCustomBlock("teleporter_frame", CustomBlockItem.TELEPORTER_FRAME.createItemStack()))
        val TEST_GUI = registry.register(TestGuiBlock())
        val HARVEST = registry.register(HarvestBlock())
    }

    open fun toItem(): CustomBlockItem? = null

    open fun createBlockEntity(pos: BlockPos): CustomBlockEntity = CustomBlockEntity(blockEntityType, pos)
}
