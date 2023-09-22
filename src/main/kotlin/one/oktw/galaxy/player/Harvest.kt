/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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
import net.minecraft.util.Hand
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.util.HarvestUtil.isMature

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
            val block = blockState.block
            val ageProperties = when (block) {
                WHEAT, CARROTS, POTATOES -> CropBlock.AGE
                BEETROOTS -> BeetrootsBlock.AGE
                COCOA -> CocoaBlock.AGE
                NETHER_WART -> NetherWartBlock.AGE
                PUMPKIN, MELON -> null
                else -> return
            }
            player.swingHand(Hand.MAIN_HAND, true)
            world.breakBlock(blockPos, false)
            Block.dropStacks(blockState, world, blockPos, world.getBlockEntity(blockPos), player, player.mainHandStack) // Fortune drop
            if (block != PUMPKIN && block != MELON) {
                world.setBlockState(blockPos, blockState.with(ageProperties, 0))
                world.updateNeighbors(blockPos, block)
            }
            justHarvested.add(player)
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (event.player in justHarvested) event.cancel = true
    }
}
