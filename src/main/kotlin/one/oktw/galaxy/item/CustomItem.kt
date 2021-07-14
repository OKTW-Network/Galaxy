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

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import one.oktw.galaxy.util.CustomRegistry
import one.oktw.galaxy.util.Registrable

abstract class CustomItem(override val identifier: Identifier, private val baseItem: Item, private val modelData: Int) : Registrable {
    companion object {
        val registry = CustomRegistry<CustomItem>()

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

    open fun writeCustomNbt(tag: NbtCompound) {
        tag.putString("CustomItemIdentifier", identifier.toString())
    }

    open fun readCustomNbt(tag: NbtCompound): CustomItem {
        require(tag.getString("CustomItemIdentifier") == identifier.toString())

        return this
    }

    open fun createItemStack(): ItemStack {
        if (cacheable && this::cacheItemStack.isInitialized) return cacheItemStack.copy()

        val itemStack = ItemStack(baseItem).apply {
            orCreateNbt.apply {
                putInt("HideFlags", ItemStack.TooltipSection.values().map(ItemStack.TooltipSection::getFlag).reduce { acc, i -> acc or i }) // ALL
                putInt("CustomModelData", modelData)
                putBoolean("Unbreakable", true)
                put("AttributeModifiers", NbtList())
            }
            setCustomName(this@CustomItem.getName())
            writeCustomNbt(getOrCreateSubNbt("GalaxyData"))
        }

        return if (cacheable) itemStack.also { cacheItemStack = it } else itemStack
    }
}
