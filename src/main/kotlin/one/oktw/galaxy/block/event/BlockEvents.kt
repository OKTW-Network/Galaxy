/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

package one.oktw.galaxy.block.event

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.network.ServerPlayerEntity
import one.oktw.galaxy.block.CustomBlockHelper
import one.oktw.galaxy.block.entity.ModelCustomBlockEntity
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock
import one.oktw.galaxy.item.CustomItemHelper
import one.oktw.galaxy.item.Tool
import java.util.*

class BlockEvents {
    private val usedLock = WeakHashMap<ServerPlayerEntity, Int>()

    init {
        ServerTickEvents.END_WORLD_TICK.register(
            ServerTickEvents.EndWorldTick {
                usedLock.entries.removeIf { (_, v) -> v + 3 < it.server.ticks } // Packet task max delay 3 tick
            }
        )
    }

    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val packet = event.packet
        val player = event.player
        if (usedLock.contains(player)) {
            event.cancel = true
            return
        }

        // CustomBlockClickListener
        if (!player.isSneaking && player.getWorld().getBlockEntity(packet.blockHitResult.blockPos) is CustomBlockClickListener) {
            usedLock[player] = player.server.ticks
        }
    }

    @EventListener(true)
    fun onUseItemOnBlock(event: PlayerUseItemOnBlock) {
        val item = event.context.stack
        val player = event.context.player as ServerPlayerEntity

        // Place custom block
        if (CustomBlockHelper.place(ItemPlacementContext(event.context))) {
            event.swing = true
            usedLock[player] = player.server.ticks
            return
        }

        // Wrench
        if (player.isSneaking && CustomItemHelper.getItem(item) == Tool.WRENCH) {
            val world = player.getWorld()
            val blockPos = event.context.blockPos
            if (world.getBlockEntity(blockPos) !is ModelCustomBlockEntity) return // Check is custom block
            CustomBlockHelper.destroyAndDrop(world, blockPos)
            event.swing = true
            usedLock[player] = player.server.ticks
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (usedLock.contains(event.player)) {
            event.cancel = true
            usedLock.remove(event.player) // Interact item is the last interactive event
        }
    }
}
