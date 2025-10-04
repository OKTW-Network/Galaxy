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
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import one.oktw.galaxy.datagen.util.ShapedRecipeJsonBuilder
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.Tool
import java.util.concurrent.CompletableFuture

class GalaxyRecipeProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registriesFuture) {
    override fun getRecipeGenerator(registries: RegistryWrapper.WrapperLookup, exporter: RecipeExporter) = object : RecipeGenerator(registries, exporter) {
        override fun generate() {
            val itemLookup = registries.getOrThrow(RegistryKeys.ITEM)

            // Block
            createShapeless(RecipeCategory.BUILDING_BLOCKS, CustomBlockItem.ELEVATOR.createItemStack())
                .input(Items.ENDER_PEARL)
                .input(Items.IRON_BLOCK)
                .criterion(hasItem(Items.ENDER_PEARL), conditionsFromItem(Items.ENDER_PEARL))
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .offerTo(exporter, "block/elevator")
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
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .criterion(hasItem(Items.DISPENSER), conditionsFromItem(Items.DISPENSER))
                .criterion(hasItem(Items.OBSERVER), conditionsFromItem(Items.OBSERVER))
                .offerTo(exporter, "block/harvest")
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
                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .criterion(hasItem(Items.LAPIS_LAZULI), conditionsFromItem(Items.LAPIS_LAZULI))
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                .criterion(hasItem(Items.OBSIDIAN), conditionsFromItem(Items.OBSIDIAN))
                .offerTo(exporter, "block/ht_crafting_table")
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
                .criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
                .criterion(hasItem(Items.CACTUS), conditionsFromItem(Items.CACTUS))
                .criterion(hasItem(Items.TERRACOTTA), conditionsFromItem(Items.TERRACOTTA))
                .criterion(hasItem(Items.SAND), conditionsFromTag(ItemTags.SAND))
                .offerTo(exporter, "block/trashcan")

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
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, "tool/crowbar")
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
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, "tool/wrench")
        }
    }

    override fun getName() = "one.oktw.galaxy.GalaxyRecipeProvider"
}
