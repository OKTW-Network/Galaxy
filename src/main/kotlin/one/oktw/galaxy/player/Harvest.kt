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

package one.oktw.galaxy.player

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.Block
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.util.HarvestUtil
import one.oktw.galaxy.util.HarvestUtil.isMature

class Harvest {
    private val justHarvested = HashSet<ServerPlayer>()

    init {
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick { justHarvested.clear() })
    }

    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val player = event.player
        if (player in justHarvested) event.cancel = true

        val world = player.level()
        val blockPos = event.packet.hitResult.blockPos
        val blockState = world.getBlockState(blockPos)

        if (
            event.packet.hand == InteractionHand.MAIN_HAND &&
            (!player.isShiftKeyDown || (player.mainHandItem.isEmpty && player.offhandItem.isEmpty)) &&
            isMature(world, blockPos, blockState)
        ) {
            event.cancel = true
            val ageProperties = HarvestUtil.getAgeProp(blockState.block)
            player.swing(InteractionHand.MAIN_HAND, true)
            world.destroyBlock(blockPos, false)
            Block.dropResources(blockState, world, blockPos, null, player, player.mainHandItem) // Fortune drop
            if (ageProperties != null) {
                world.setBlockAndUpdate(blockPos, blockState.setValue(ageProperties, 0))
                world.updateNeighborsAt(blockPos, blockState.block)
            }
            justHarvested.add(player)
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (event.player in justHarvested) event.cancel = true
    }
}
