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

package one.oktw.galaxy.recipe.tools

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Items.*
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.world.World
import one.oktw.galaxy.item.Tool

class Wrench : CraftingRecipe {
    private val item = Tool.WRENCH.createItemStack()
    private val list = listOf(
        IRON_INGOT, AIR, IRON_INGOT,
        AIR, STICK, AIR,
        AIR, IRON_INGOT, AIR
    )

    override fun matches(inv: CraftingInventory, world: World): Boolean {
        for (x in 0 until inv.width) {
            for (y in 0 until inv.height) {
                val index = x + y * inv.width
                if (inv.getStack(index).item != list[index]) return false
            }
        }

        return true
    }

    override fun craft(inv: CraftingInventory) = Tool.WRENCH.createItemStack()

    @Environment(EnvType.CLIENT)
    override fun fits(width: Int, height: Int): Boolean {
        throw NotImplementedError()
    }

    override fun getOutput() = item

    override fun getId() = Identifier("galaxy", "item/wrench")

    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented, support client mod.")
    }
}
