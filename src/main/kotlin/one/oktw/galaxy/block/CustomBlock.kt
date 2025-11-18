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

package one.oktw.galaxy.block

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.entity.BlockEntityType
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.util.CustomRegistry
import one.oktw.galaxy.util.Registrable

open class CustomBlock(final override val identifier: ResourceLocation, val baseBlock: Block = BARRIER) : Registrable {
    constructor(id: String, baseBlock: Block = BARRIER) : this(ResourceLocation.fromNamespaceAndPath("galaxy", "block/$id"), baseBlock)

    protected val blockEntityType: BlockEntityType<CustomBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        identifier,
        FabricBlockEntityTypeBuilder.create({ pos, _ -> createBlockEntity(pos) }, BARRIER).build()
    )

    companion object {
        val registry = CustomRegistry<CustomBlock>()

        val DUMMY = registry.register(DummyBlock())
        val CONTROL_PANEL = registry.register(CustomBlock("control_panel", baseBlock = COMPARATOR))
        val PLANET_TERMINAL = registry.register(CustomBlock("planet_terminal", baseBlock = BEACON))
        val HT_CRAFTING_TABLE = registry.register(HTCraftingTable())
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
