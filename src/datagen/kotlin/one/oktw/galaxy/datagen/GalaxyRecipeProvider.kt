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

package one.oktw.galaxy.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import one.oktw.galaxy.datagen.util.ShapedRecipeJsonBuilder
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.Tool
import java.util.concurrent.CompletableFuture

class GalaxyRecipeProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricRecipeProvider(output, registriesFuture) {
    override fun createRecipeProvider(registries: HolderLookup.Provider, exporter: RecipeOutput) = object : RecipeProvider(registries, exporter) {
        override fun buildRecipes() {
            val itemLookup = registries.lookupOrThrow(Registries.ITEM)

            // Block
            shapeless(RecipeCategory.BUILDING_BLOCKS, CustomBlockItem.ELEVATOR.createItemStack())
                .requires(Items.ENDER_PEARL)
                .requires(Items.IRON_BLOCK)
                .unlockedBy(getHasName(Items.ENDER_PEARL), has(Items.ENDER_PEARL))
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
                .save(exporter, "block/elevator")
            ShapedRecipeJsonBuilder(itemLookup, RecipeCategory.BUILDING_BLOCKS, CustomBlockItem.HARVEST.createItemStack())
                .addInput('C', Items.COPPER_INGOT)
                .addInput('D', Items.DISPENSER)
                .addInput('O', Items.OBSERVER)
                .setPattern(
                    listOf(
                        "CCC",
                        "CDO",
                        "CCC"
                    )
                )
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .unlockedBy(getHasName(Items.DISPENSER), has(Items.DISPENSER))
                .unlockedBy(getHasName(Items.OBSERVER), has(Items.OBSERVER))
                .save(exporter, "block/harvest")
            ShapedRecipeJsonBuilder(itemLookup, RecipeCategory.BUILDING_BLOCKS, CustomBlockItem.HT_CRAFTING_TABLE.createItemStack())
                .addInput('R', Items.REDSTONE)
                .addInput('D', Items.DIAMOND)
                .addInput('L', Items.LAPIS_LAZULI)
                .addInput('I', Items.IRON_INGOT)
                .addInput('C', Items.CRAFTING_TABLE)
                .addInput('O', Items.OBSIDIAN)
                .setPattern(
                    listOf(
                        "RDL",
                        "ICI",
                        "LOR"
                    )
                )
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Items.LAPIS_LAZULI))
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                .unlockedBy(getHasName(Items.OBSIDIAN), has(Items.OBSIDIAN))
                .save(exporter, "block/ht_crafting_table")
            ShapedRecipeJsonBuilder(itemLookup, RecipeCategory.BUILDING_BLOCKS, CustomBlockItem.TRASHCAN.createItemStack())
                .addInput('G', Items.GLASS)
                .addInput('C', Items.CACTUS)
                .addInput('T', Items.TERRACOTTA)
                .addInput('S', ItemTags.SAND)
                .setPattern(
                    listOf(
                        "GGG",
                        "GCG",
                        "TST"
                    )
                )
                .unlockedBy(getHasName(Items.GLASS), has(Items.GLASS))
                .unlockedBy(getHasName(Items.CACTUS), has(Items.CACTUS))
                .unlockedBy(getHasName(Items.TERRACOTTA), has(Items.TERRACOTTA))
                .unlockedBy(getHasName(Items.SAND), has(ItemTags.SAND))
                .save(exporter, "block/trashcan")

            // Tool
            ShapedRecipeJsonBuilder(itemLookup, RecipeCategory.TOOLS, Tool.CROWBAR.createItemStack())
                .addInput('I', Items.IRON_INGOT)
                .setPattern(
                    listOf(
                        "II",
                        " I",
                        " I"
                    )
                )
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(exporter, "tool/crowbar")
            ShapedRecipeJsonBuilder(itemLookup, RecipeCategory.TOOLS, Tool.WRENCH.createItemStack())
                .addInput('I', Items.IRON_INGOT)
                .addInput('S', Items.STICK)
                .setPattern(
                    listOf(
                        "I I",
                        " S ",
                        " I "
                    )
                )
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(exporter, "tool/wrench")
        }
    }

    override fun getName() = "one.oktw.galaxy.GalaxyRecipeProvider"
}
