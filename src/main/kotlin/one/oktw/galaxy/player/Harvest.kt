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

package one.oktw.galaxy.player

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import net.minecraft.block.*
import net.minecraft.block.Blocks.*
import net.minecraft.client.network.packet.BlockUpdateS2CPacket
import net.minecraft.client.network.packet.EntityAnimationS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.IntProperty
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.RayTraceContext
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.network.ItemFunctionAccessor
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class Harvest {
    private val justHarvested = ConcurrentHashMap<ServerPlayerEntity, Boolean>()
    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val world = main!!.server.getWorld(event.player.dimension)
        val hand = event.packet.hand

        val blockHitResult = event.packet.hitY
        val blockState = world.getBlockState(blockHitResult.blockPos)

        val isMature = isMature(world, blockHitResult.blockPos, blockState)

        if (isMature) {
            event.cancel = true
            if (hand != Hand.MAIN_HAND) {
                return
            }
            val ageProperties = when (blockState.block) {
                WHEAT, CARROTS, POTATOES -> CropBlock.AGE
                BEETROOTS -> BeetrootsBlock.AGE
                COCOA -> CocoaBlock.AGE
                NETHER_WART -> NetherWartBlock.AGE
                else -> IntProperty.of("AGE", 0, 1)
            }
            GlobalScope.launch {
                withContext(event.player.server.asCoroutineDispatcher()) {
                    event.player.swingHand(hand)
                    event.player.networkHandler.sendPacket(EntityAnimationS2CPacket(event.player, if (hand == Hand.MAIN_HAND) 0 else 3))
                    world.breakBlock(blockHitResult.blockPos, true)
                    if (blockState.block != PUMPKIN && blockState.block != MELON) {
                        world.setBlockState(blockHitResult.blockPos, blockState.with(ageProperties, 0))
                        world.updateNeighbors(blockHitResult.blockPos, blockState.block)
                        updateBlockAndInventory(event.player, world, blockHitResult.blockPos)
                    }
                }
                justHarvested[event.player] = true
                delay(Duration.ofSeconds(1))
                justHarvested.remove(event.player)
            }
            return
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        val world = main!!.server.getWorld(event.player.dimension)

        val itemStack = event.player.getStackInHand(event.packet.hand)
        val item = itemStack.item as ItemFunctionAccessor
        val blockHitResult = item.getRayTrace(world, event.player, RayTraceContext.FluidHandling.ANY) as BlockHitResult
        val blockState = world.getBlockState(blockHitResult.blockPos)

        val harvested = justHarvested[event.player] ?: false
        if (isMature(world, blockHitResult.blockPos, blockState) || harvested) {
            event.cancel = true
            updateBlockAndInventory(event.player, world, blockHitResult.blockPos)
        }
    }

    private fun isMature(world: ServerWorld, blockPos: BlockPos, blockState: BlockState): Boolean = when (blockState.block) {
        WHEAT, CARROTS, POTATOES -> blockState.let((blockState.block as CropBlock)::isMature)
        BEETROOTS -> blockState.let((blockState.block as BeetrootsBlock)::isMature)
        COCOA -> blockState[CocoaBlock.AGE] >= 2
        NETHER_WART -> blockState[NetherWartBlock.AGE] >= 3
        MELON -> isNextTo(world, blockPos, ATTACHED_MELON_STEM)
        PUMPKIN -> isNextTo(world, blockPos, ATTACHED_PUMPKIN_STEM)
        else -> false
    }

    private fun updateBlockAndInventory(player: ServerPlayerEntity, world: ServerWorld, blockPos: BlockPos) {
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(1, 0, 0)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, 0, 1)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(-1, 0, 0)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, 0, -1)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, 1, 0)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, -1, 0)))
        player.onContainerRegistered(player.container, player.container.stacks)

    }

    private fun isNextTo(world: ServerWorld, blockPos: BlockPos, block: Block): Boolean {
        return world.getBlockState(blockPos.add(1, 0, 0)).block == block ||
            world.getBlockState(blockPos.add(0, 0, 1)).block == block ||
            world.getBlockState(blockPos.add(-1, 0, 0)).block == block ||
            world.getBlockState(blockPos.add(0, 0, -1)).block == block
    }
}
