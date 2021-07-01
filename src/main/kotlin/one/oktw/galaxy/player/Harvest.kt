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

package one.oktw.galaxy.player

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.block.*
import net.minecraft.block.Blocks.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties.HORIZONTAL_FACING
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent

class Harvest {
    private val justHarvested = HashSet<ServerPlayerEntity>()

    init {
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick { justHarvested.clear() })
    }

    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val player = event.player
        if (player in justHarvested) event.cancel = true

        val world = player.serverWorld
        val blockPos = event.packet.blockHitResult.blockPos
        val blockState = world.getBlockState(blockPos)

        if (event.packet.hand == Hand.MAIN_HAND && !player.isSneaking && isMature(world, blockPos, blockState)) {
            event.cancel = true
            val ageProperties = when (blockState.block) {
                WHEAT, CARROTS, POTATOES -> CropBlock.AGE
                BEETROOTS -> BeetrootsBlock.AGE
                COCOA -> CocoaBlock.AGE
                NETHER_WART -> NetherWartBlock.AGE
                else -> IntProperty.of("AGE", 0, 1)
            }
            player.swingHand(Hand.MAIN_HAND, true)
            world.breakBlock(blockPos, false)
            Block.dropStacks(blockState, world, blockPos, world.getBlockEntity(blockPos), player, player.mainHandStack) // Fortune drop
            if (blockState.block != PUMPKIN && blockState.block != MELON) {
                world.setBlockState(blockPos, blockState.with(ageProperties, 0))
                world.updateNeighbors(blockPos, blockState.block)
            }
            justHarvested.add(player)
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (event.player in justHarvested) event.cancel = true
    }

    private fun isMature(world: ServerWorld, blockPos: BlockPos, blockState: BlockState): Boolean = when (blockState.block) {
        WHEAT, CARROTS, POTATOES, BEETROOTS -> blockState.let((blockState.block as CropBlock)::isMature)
        COCOA -> blockState[CocoaBlock.AGE] >= 2
        NETHER_WART -> blockState[NetherWartBlock.AGE] >= 3
        MELON -> isNextTo(world, blockPos, ATTACHED_MELON_STEM)
        PUMPKIN -> isNextTo(world, blockPos, ATTACHED_PUMPKIN_STEM)
        else -> false
    }

    private fun isNextTo(world: ServerWorld, blockPos: BlockPos, block: Block): Boolean =
        (world.getBlockState(blockPos.east()).block == block && isPaired(world, blockPos, blockPos.east())) ||
        (world.getBlockState(blockPos.west()).block == block && isPaired(world, blockPos, blockPos.west())) ||
        (world.getBlockState(blockPos.north()).block == block && isPaired(world, blockPos, blockPos.north())) ||
        (world.getBlockState(blockPos.south()).block == block && isPaired(world, blockPos, blockPos.south()))

    private fun isPaired(world: ServerWorld, blockPos: BlockPos, stemPos: BlockPos): Boolean {
        val connectedPos = world.getBlockState(stemPos)
            .entries[HORIZONTAL_FACING]
            .let {
                when (it) {
                    Direction.EAST -> stemPos.east()
                    Direction.WEST -> stemPos.west()
                    Direction.NORTH -> stemPos.north()
                    Direction.SOUTH -> stemPos.south()
                    else -> null
                }
            }

        return connectedPos == blockPos
    }
}
