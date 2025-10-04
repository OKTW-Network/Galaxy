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
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import one.oktw.galaxy.util.CustomRegistry
import one.oktw.galaxy.util.Registrable

abstract class CustomItem(
    override val identifier: Identifier,
    private val baseItem: Item,
    private val itemModel: Identifier = identifier,
    private val customModelData: CustomModelDataComponent? = null,
    private val maxStackSize: Int = 64,
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

    abstract fun getName(): Text?

    open fun writeCustomNbt(nbt: NbtCompound) {
        nbt.putString("custom_item_identifier", identifier.toString())
    }

    open fun readCustomNbt(nbt: NbtCompound): CustomItem {
        require(nbt.getString("custom_item_identifier", "") == identifier.toString())

        return this
    }

    open fun createItemStack(): ItemStack {
        if (cacheable && this::cacheItemStack.isInitialized) return cacheItemStack.copy()

        return ItemStack(baseItem).apply {
            set(DataComponentTypes.ITEM_MODEL, itemModel)
            set(DataComponentTypes.UNBREAKABLE, net.minecraft.util.Unit.INSTANCE)
            set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent(emptyList()))
            set(DataComponentTypes.ITEM_NAME, this@CustomItem.getName())
            set(DataComponentTypes.RARITY, Rarity.COMMON)
            set(DataComponentTypes.MAX_STACK_SIZE, maxStackSize)
            set(DataComponentTypes.RARITY, Rarity.COMMON)
            set(
                DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplayComponent(hideTooltip, ReferenceSortedSets.emptySet())
                    .with(DataComponentTypes.ATTRIBUTE_MODIFIERS, true)
                    .with(DataComponentTypes.UNBREAKABLE, true)
                    .with(DataComponentTypes.ENCHANTMENTS, true)
            )
            if (customModelData != null) {
                set(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData)
            }

            // Galaxy Data
            val galaxyNbt = CustomItemHelper.getNbt(this)
            writeCustomNbt(galaxyNbt)
            apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { component ->
                component.apply { nbt ->
                    nbt.put("galaxy_data", galaxyNbt)
                }
            }
        }.also { if (cacheable) cacheItemStack = it.copy() }
    }
}
