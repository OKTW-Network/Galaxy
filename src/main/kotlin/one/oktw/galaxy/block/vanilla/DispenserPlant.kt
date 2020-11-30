/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

package one.oktw.galaxy.block.vanilla

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.DispenserBlock
import net.minecraft.block.dispenser.ItemDispenserBehavior
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPointer
import net.minecraft.util.math.Direction
import net.minecraft.world.World


object DispenserPlant {
    private fun plant(blockPointer: BlockPointer, itemStack: ItemStack, block: Block, validBlocksToPlantOn: List<Block>): ItemStack {
        val world: World = blockPointer.world
        val dispenserFacing = blockPointer.blockState.get(DispenserBlock.FACING)

        val currentBlockPos = blockPointer.blockPos.offset(dispenserFacing)
        val currentBlockState = world.getBlockState(currentBlockPos)

        val plantBlockPos = if (dispenserFacing == Direction.UP) currentBlockPos.up(1) else currentBlockPos.down(1)
        val plantBlockState = world.getBlockState(plantBlockPos)

        if (block == Blocks.RED_MUSHROOM || block == Blocks.BROWN_MUSHROOM) {
            if (dispenserFacing == Direction.UP) {
                if (plantBlockState.block == Blocks.AIR) {
                    world.setBlockState(plantBlockPos, block.defaultState)
                    itemStack.decrement(1)
                    return itemStack
                }
            } else {
                if (currentBlockState.block == Blocks.AIR) {
                    world.setBlockState(currentBlockPos, block.defaultState)
                    itemStack.decrement(1)
                    return itemStack
                }
            }
        }

        if  (validBlocksToPlantOn.contains(currentBlockState.block) && plantBlockState.block == Blocks.AIR) {
            world.setBlockState(plantBlockPos, block.defaultState)
            itemStack.decrement(1)
            return itemStack
        }

        if (validBlocksToPlantOn.contains(plantBlockState.block) && currentBlockState.block == Blocks.AIR) {
            world.setBlockState(currentBlockPos, block.defaultState)
            itemStack.decrement(1)
            return itemStack
        }

        return ItemDispenserBehavior().dispense(blockPointer, itemStack)
    }

    private fun seedsDispenserBehavior(block: Block, dependent: List<Block>): ItemDispenserBehavior = object : ItemDispenserBehavior() {
        override fun dispenseSilently(blockPointer: BlockPointer, itemStack: ItemStack): ItemStack {
            return plant(blockPointer, itemStack, block, dependent)
        }
    }

    fun register() {
        DispenserBlock.registerBehavior(Items.WHEAT_SEEDS, seedsDispenserBehavior(Blocks.WHEAT, listOf(Blocks.FARMLAND)))
        DispenserBlock.registerBehavior(Items.POTATO, seedsDispenserBehavior(Blocks.POTATOES, listOf(Blocks.FARMLAND)))
        DispenserBlock.registerBehavior(Items.CARROT, seedsDispenserBehavior(Blocks.CARROTS, listOf(Blocks.FARMLAND)))
        DispenserBlock.registerBehavior(Items.MELON_SEEDS, seedsDispenserBehavior(Blocks.MELON_STEM, listOf(Blocks.FARMLAND)))
        DispenserBlock.registerBehavior(Items.PUMPKIN_SEEDS, seedsDispenserBehavior(Blocks.PUMPKIN_STEM, listOf(Blocks.FARMLAND)))
        DispenserBlock.registerBehavior(Items.BEETROOT_SEEDS, seedsDispenserBehavior(Blocks.BEETROOTS, listOf(Blocks.FARMLAND)))
        DispenserBlock.registerBehavior(Items.NETHER_WART, seedsDispenserBehavior(Blocks.NETHER_WART, listOf(Blocks.SOUL_SAND)))
        DispenserBlock.registerBehavior(Items.SWEET_BERRIES, seedsDispenserBehavior(Blocks.SWEET_BERRY_BUSH, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.FARMLAND)))
        DispenserBlock.registerBehavior(Items.OAK_SAPLING, seedsDispenserBehavior(Blocks.OAK_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL)))
        DispenserBlock.registerBehavior(Items.BIRCH_SAPLING, seedsDispenserBehavior(Blocks.BIRCH_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL)))
        DispenserBlock.registerBehavior(Items.SPRUCE_SAPLING, seedsDispenserBehavior(Blocks.SPRUCE_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL)))
        DispenserBlock.registerBehavior(Items.JUNGLE_SAPLING, seedsDispenserBehavior(Blocks.JUNGLE_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL)))
        DispenserBlock.registerBehavior(Items.ACACIA_SAPLING, seedsDispenserBehavior(Blocks.ACACIA_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL)))
        DispenserBlock.registerBehavior(Items.DARK_OAK_SAPLING, seedsDispenserBehavior(Blocks.DARK_OAK_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL)))
        DispenserBlock.registerBehavior(Items.SUGAR_CANE, seedsDispenserBehavior(Blocks.SUGAR_CANE, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.SAND, Blocks.RED_SAND, Blocks.COARSE_DIRT)))
        DispenserBlock.registerBehavior(Items.CHORUS_FLOWER, seedsDispenserBehavior(Blocks.CHORUS_FLOWER, listOf(Blocks.END_STONE)))
        DispenserBlock.registerBehavior(Items.BAMBOO, seedsDispenserBehavior(Blocks.BAMBOO, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.SAND, Blocks.RED_SAND, Blocks.COARSE_DIRT, Blocks.GRAVEL, Blocks.MYCELIUM)))
        DispenserBlock.registerBehavior(Items.RED_MUSHROOM, seedsDispenserBehavior(Blocks.RED_MUSHROOM, listOf()))
        DispenserBlock.registerBehavior(Items.BROWN_MUSHROOM, seedsDispenserBehavior(Blocks.BROWN_MUSHROOM, listOf()))
        DispenserBlock.registerBehavior(Items.CRIMSON_FUNGUS, seedsDispenserBehavior(Blocks.CRIMSON_FUNGUS, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.FARMLAND, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL, Blocks.MYCELIUM)))
        DispenserBlock.registerBehavior(Items.WARPED_FUNGUS, seedsDispenserBehavior(Blocks.WARPED_FUNGUS, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.FARMLAND, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL, Blocks.MYCELIUM)))
    }
}
