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

import net.minecraft.component.DataComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.component.type.UnbreakableComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import one.oktw.galaxy.mixin.accessor.DataComponentTypesAccessor
import one.oktw.galaxy.util.CustomRegistry
import one.oktw.galaxy.util.Registrable

abstract class CustomItem(override val identifier: Identifier, private val baseItem: Item, private val modelData: Int) : Registrable {
    companion object {
        val registry = CustomRegistry<CustomItem>()

        val galaxyDataComponent: DataComponentType<NbtComponent> = DataComponentTypesAccessor.invokeRegister("galaxy_data") { builder ->
            builder.codec(NbtComponent.CODEC)
        }

        init {
            Button
            Gui
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
        nbt.putString("CustomItemIdentifier", identifier.toString())
    }

    open fun readCustomNbt(nbt: NbtCompound): CustomItem {
        require(nbt.getString("CustomItemIdentifier") == identifier.toString())

        return this
    }

    open fun createItemStack(): ItemStack {
        if (cacheable && this::cacheItemStack.isInitialized) return cacheItemStack.copy()

        return ItemStack(baseItem).apply {
            set(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent(modelData))
            set(DataComponentTypes.UNBREAKABLE, UnbreakableComponent(false))
            set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent(listOf<AttributeModifiersComponent.Entry>(), false))
            set(DataComponentTypes.ITEM_NAME, this@CustomItem.getName())

            // Galaxy Data
            val galaxyNbt = CustomItemHelper.getNbt(this) ?: NbtComponent.DEFAULT.copyNbt()
            writeCustomNbt(galaxyNbt)
            set(galaxyDataComponent, NbtComponent.of(galaxyNbt))
        }.also { if (cacheable) cacheItemStack = it.copy() }
    }
}
