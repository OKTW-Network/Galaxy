/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.block.util

import net.minecraft.block.*
import net.minecraft.block.Blocks.AIR
import net.minecraft.block.Blocks.BARRIER
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.TntEntity
import net.minecraft.entity.vehicle.AbstractMinecartEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.GameMode
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.item.type.ItemType
import net.minecraft.block.Block as minecraftBlock

object CustomBlockUtil {
    fun placeAndRegisterBlock(world: ServerWorld, blockPos: BlockPos, blockItem: ItemStack, blockType: BlockType): Boolean {
        val entities = world.getEntities(null, Box(blockPos))
        if (entities.any { entity -> entity is LivingEntity || entity is BoatEntity || entity is AbstractMinecartEntity || entity is TntEntity }) {
            return false
        }
        if (!isReplaceable(world, blockPos)) return false
        if (world.setBlockState(blockPos, BARRIER.defaultState)) {
            CustomBlockEntityBuilder()
                .setBlockItem(blockItem)
                .setBlockType(blockType)
                .setPosition(blockPos)
                .setWorld(world)
                .setSmall()
                .create()
            playSound(world, blockPos)
            return true
        }
        return false
    }

    fun registerBlock(world: ServerWorld, blockPos: BlockPos, blockType: BlockType): Boolean {
        val entity = getCustomBlockEntity(world, blockPos)
        if (entity != null) return false
        CustomBlockEntityBuilder()
            .setBlockType(blockType)
            .setPosition(blockPos)
            .setWorld(world)
            .setSmall()
            .create()
        return true
    }

    fun removeBlock(world: ServerWorld, blockPos: BlockPos) {
        val entity = getCustomBlockEntity(world, blockPos) ?: return
        val block = Block(getTypeFromCustomBlockEntity(entity) ?: return)
        world.setBlockState(blockPos, AIR.defaultState)
        entity.kill()
        minecraftBlock.dropStack(world, blockPos, block.item!!.createItemStack())
    }

    fun unregisterBlock(world: ServerWorld, blockPos: BlockPos): Boolean {
        val entity = getCustomBlockEntity(world, blockPos) ?: return false
        entity.kill()
        return true
    }

    fun getCustomBlockEntity(world: ServerWorld, blockPos: BlockPos): Entity? {
        val entities = world.getEntities(null, Box(blockPos))
        return entities.firstOrNull { entity -> entity.scoreboardTags.contains("BLOCK") }
    }

    fun getTypeFromCustomBlockEntity(entity: Entity): BlockType? {
        val tag = entity.scoreboardTags.firstOrNull { string -> BlockType.values().map { it.name }.contains(string) }
        return if (tag != null) BlockType.valueOf(tag) else null
    }

    fun getPlacePosition(world: ServerWorld, blockPos: BlockPos, blockHitResult: BlockHitResult): BlockPos =
        if (isReplaceable(world, blockPos)) blockPos else blockPos.offset(blockHitResult.side)

    fun vanillaTryUseBlock(player: ServerPlayerEntity, hand: Hand, hitResult: BlockHitResult): ActionResult {
        // Vanilla
        if (player.interactionManager.gameMode != GameMode.SPECTATOR) {
            if (!((!player.mainHandStack.isEmpty || !player.offHandStack.isEmpty) && player.shouldCancelInteraction())) {
                val world = player.serverWorld
                val tryUseBlock = world.getBlockState(hitResult.blockPos).onUse(world, player, hand, hitResult)
                if (tryUseBlock.isAccepted) {
                    return tryUseBlock
                } else {
                    // Prevent duplicate
                    val itemStack = player.getStackInHand(hand)
                    return if (!itemStack.isEmpty && !player.itemCooldownManager.isCoolingDown(itemStack.item)) {
                        val itemUsageContext = ItemUsageContext(player, hand, hitResult)
                        // Vanilla stops here and check if it is custom block item first
                        val tag = itemStack.tag
                        val itemType = tag?.getString("customItemType")
                        if (itemType == ItemType.BLOCK.name) return ActionResult.PASS
                        // Vanilla continue
                        if (player.interactionManager.isCreative) {
                            val count = itemStack.count
                            val tryUseOnBlock = itemStack.useOnBlock(itemUsageContext)
                            itemStack.count = count
                            tryUseOnBlock
                        } else {
                            itemStack.useOnBlock(itemUsageContext)
                        }
                    } else {
                        ActionResult.PASS
                    }
                }
            }
        }
        return ActionResult.PASS
    }

    private fun isReplaceable(world: ServerWorld, blockPos: BlockPos) = when (world.getBlockState(blockPos).block) {
        is TallFlowerBlock -> false
        is AirBlock, is FernBlock, is DeadBushBlock, is SeagrassBlock, is VineBlock, is TallSeagrassBlock, is TallPlantBlock, is FluidBlock -> true
        is SnowBlock -> world.getBlockState(blockPos)[Properties.LAYERS] == 1
        else -> false
    }

    private fun playSound(world: ServerWorld, blockPos: BlockPos) {
        world.playSound(null, blockPos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
    }
}
