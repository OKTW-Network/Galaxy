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
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPointer
import net.minecraft.util.math.Direction
import net.minecraft.world.World


object DispenserPlant {
    private fun plant(blockPointer: BlockPointer, itemStack: ItemStack, block: Block, validBlocksToPlantOn: List<Block>, soundEvent: SoundEvent): ItemStack {
        val world: World = blockPointer.world
        val dispenserFacing = blockPointer.blockState.get(DispenserBlock.FACING)

        val currentBlockPos = blockPointer.blockPos.offset(dispenserFacing)
        val currentBlockState = world.getBlockState(currentBlockPos)

        val plantBlockPos = if (dispenserFacing == Direction.UP) currentBlockPos.up(1) else currentBlockPos.down(1)
        val plantBlockState = world.getBlockState(plantBlockPos)

        if (block == Blocks.RED_MUSHROOM || block == Blocks.BROWN_MUSHROOM) {
            if (dispenserFacing == Direction.UP) {
                if (plantBlockState.block == Blocks.AIR && world.getBaseLightLevel(plantBlockPos, 0) < 13 && currentBlockState.isOpaqueFullCube(world, currentBlockPos)) {
                    world.setBlockState(plantBlockPos, block.defaultState)
                    world.playSound(null, plantBlockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 0.8F)
                    itemStack.decrement(1)
                    return itemStack
                }
            } else {
                if (currentBlockState.block == Blocks.AIR && world.getBaseLightLevel(currentBlockPos, 0) < 13 && plantBlockState.isOpaqueFullCube(world, plantBlockPos)) {
                    world.setBlockState(currentBlockPos, block.defaultState)
                    world.playSound(null, currentBlockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 0.8F)
                    itemStack.decrement(1)
                    return itemStack
                }
            }
        }

        if (dispenserFacing == Direction.UP) {
            if  (validBlocksToPlantOn.contains(currentBlockState.block) && plantBlockState.block == Blocks.AIR) {
                world.setBlockState(plantBlockPos, block.defaultState)
                world.playSound(null, plantBlockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 0.8F)
                itemStack.decrement(1)
                return itemStack
            }
        } else {
            if (validBlocksToPlantOn.contains(plantBlockState.block) && currentBlockState.block == Blocks.AIR) {
                world.setBlockState(currentBlockPos, block.defaultState)
                world.playSound(null, currentBlockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 0.8F)
                itemStack.decrement(1)
                return itemStack
            }
        }

        return ItemDispenserBehavior().dispense(blockPointer, itemStack)
    }

    private fun plantDispenserBehavior(block: Block, dependent: List<Block>, soundEvent: SoundEvent): ItemDispenserBehavior = object : ItemDispenserBehavior() {
        override fun dispenseSilently(blockPointer: BlockPointer, itemStack: ItemStack): ItemStack {
            return plant(blockPointer, itemStack, block, dependent, soundEvent)
        }
    }

    fun register() {
        DispenserBlock.registerBehavior(Items.WHEAT_SEEDS, plantDispenserBehavior(Blocks.WHEAT, listOf(Blocks.FARMLAND), SoundEvents.ITEM_CROP_PLANT))
        DispenserBlock.registerBehavior(Items.POTATO, plantDispenserBehavior(Blocks.POTATOES, listOf(Blocks.FARMLAND), SoundEvents.ITEM_CROP_PLANT))
        DispenserBlock.registerBehavior(Items.CARROT, plantDispenserBehavior(Blocks.CARROTS, listOf(Blocks.FARMLAND), SoundEvents.ITEM_CROP_PLANT))
        DispenserBlock.registerBehavior(Items.MELON_SEEDS, plantDispenserBehavior(Blocks.MELON_STEM, listOf(Blocks.FARMLAND), SoundEvents.ITEM_CROP_PLANT))
        DispenserBlock.registerBehavior(Items.PUMPKIN_SEEDS, plantDispenserBehavior(Blocks.PUMPKIN_STEM, listOf(Blocks.FARMLAND), SoundEvents.ITEM_CROP_PLANT))
        DispenserBlock.registerBehavior(Items.BEETROOT_SEEDS, plantDispenserBehavior(Blocks.BEETROOTS, listOf(Blocks.FARMLAND), SoundEvents.ITEM_CROP_PLANT))
        DispenserBlock.registerBehavior(Items.NETHER_WART, plantDispenserBehavior(Blocks.NETHER_WART, listOf(Blocks.SOUL_SAND), SoundEvents.ITEM_NETHER_WART_PLANT))
        DispenserBlock.registerBehavior(Items.SWEET_BERRIES, plantDispenserBehavior(Blocks.SWEET_BERRY_BUSH, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.FARMLAND), SoundEvents.BLOCK_SWEET_BERRY_BUSH_PLACE))
        DispenserBlock.registerBehavior(Items.OAK_SAPLING, plantDispenserBehavior(Blocks.OAK_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.FARMLAND), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.BIRCH_SAPLING, plantDispenserBehavior(Blocks.BIRCH_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.FARMLAND), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.SPRUCE_SAPLING, plantDispenserBehavior(Blocks.SPRUCE_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.FARMLAND), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.JUNGLE_SAPLING, plantDispenserBehavior(Blocks.JUNGLE_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.FARMLAND), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.ACACIA_SAPLING, plantDispenserBehavior(Blocks.ACACIA_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.FARMLAND), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.DARK_OAK_SAPLING, plantDispenserBehavior(Blocks.DARK_OAK_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.FARMLAND), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.SUGAR_CANE, plantDispenserBehavior(Blocks.SUGAR_CANE, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.SAND, Blocks.RED_SAND, Blocks.COARSE_DIRT), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.CHORUS_FLOWER, plantDispenserBehavior(Blocks.CHORUS_FLOWER, listOf(Blocks.END_STONE), SoundEvents.BLOCK_WOOD_PLACE))
        DispenserBlock.registerBehavior(Items.BAMBOO, plantDispenserBehavior(Blocks.BAMBOO_SAPLING, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.SAND, Blocks.RED_SAND, Blocks.COARSE_DIRT, Blocks.GRAVEL, Blocks.MYCELIUM), SoundEvents.BLOCK_BAMBOO_SAPLING_PLACE))
        DispenserBlock.registerBehavior(Items.RED_MUSHROOM, plantDispenserBehavior(Blocks.RED_MUSHROOM, listOf(), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.BROWN_MUSHROOM, plantDispenserBehavior(Blocks.BROWN_MUSHROOM, listOf(), SoundEvents.BLOCK_GRASS_PLACE))
        DispenserBlock.registerBehavior(Items.CRIMSON_FUNGUS, plantDispenserBehavior(Blocks.CRIMSON_FUNGUS, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.FARMLAND, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL, Blocks.MYCELIUM), SoundEvents.BLOCK_FUNGUS_PLACE))
        DispenserBlock.registerBehavior(Items.WARPED_FUNGUS, plantDispenserBehavior(Blocks.WARPED_FUNGUS, listOf(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.FARMLAND, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL, Blocks.MYCELIUM), SoundEvents.BLOCK_FUNGUS_PLACE))
    }
}
