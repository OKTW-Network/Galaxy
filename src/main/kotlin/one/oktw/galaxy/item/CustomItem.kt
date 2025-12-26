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

import it.unimi.dsi.fastutil.objects.ReferenceSortedSets
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.TooltipDisplay
import one.oktw.galaxy.util.CustomRegistry
import one.oktw.galaxy.util.Registrable

abstract class CustomItem(
    override val identifier: ResourceLocation,
    private val baseItem: Item,
    private val itemModel: ResourceLocation = identifier,
    private val customModelData: CustomModelData? = null,
    private val maxStack: Int = 64,
    private val hideTooltip: Boolean = false,
) : Registrable {
    companion object {
        val registry = CustomRegistry<CustomItem>()

        init {
            Material
            Tool
            Upgrade
            Weapon
            CustomBlockItem
        }
    }

    open val cacheable = true
    private lateinit var cacheItemStack: ItemStack

    abstract fun getName(): Component?

    open fun writeCustomNbt(nbt: CompoundTag) {
        nbt.putString("custom_item_identifier", identifier.toString())
    }

    open fun readCustomNbt(nbt: CompoundTag): CustomItem {
        require(nbt.getStringOr("custom_item_identifier", "") == identifier.toString())

        return this
    }

    open fun createItemStack(): ItemStack {
        if (cacheable && this::cacheItemStack.isInitialized) return cacheItemStack.copy()

        return ItemStack(baseItem).apply {
            set(DataComponents.ITEM_MODEL, itemModel)
            set(DataComponents.UNBREAKABLE, net.minecraft.util.Unit.INSTANCE)
            set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers(emptyList()))
            set(DataComponents.ITEM_NAME, this@CustomItem.getName())
            set(DataComponents.RARITY, Rarity.COMMON)
            set(DataComponents.MAX_STACK_SIZE, maxStack)
            set(
                DataComponents.TOOLTIP_DISPLAY,
                TooltipDisplay(hideTooltip, ReferenceSortedSets.emptySet())
                    .withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true)
                    .withHidden(DataComponents.UNBREAKABLE, true)
                    .withHidden(DataComponents.ENCHANTMENTS, true)
            )
            if (customModelData != null) {
                set(DataComponents.CUSTOM_MODEL_DATA, customModelData)
            }

            // Galaxy Data
            val galaxyNbt = CustomItemHelper.getNbt(this)
            writeCustomNbt(galaxyNbt)
            update(DataComponents.CUSTOM_DATA, CustomData.EMPTY) { component ->
                component.update { nbt ->
                    nbt.put("galaxy_data", galaxyNbt)
                }
            }
        }.also { if (cacheable) cacheItemStack = it.copy() }
    }
}
