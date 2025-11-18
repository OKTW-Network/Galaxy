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

package one.oktw.galaxy.block.event

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.BlockPlaceContext
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
    private val usedLock = WeakHashMap<ServerPlayer, Int>()

    init {
        ServerTickEvents.END_WORLD_TICK.register(
            ServerTickEvents.EndWorldTick {
                usedLock.entries.removeIf { (_, v) -> v + 3 < it.server.tickCount } // Packet task max delay 3 tick
            }
        )
    }

    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val player = event.player
        if (usedLock.contains(player)) {
            event.cancel = true
            if (event.packet.hand == InteractionHand.OFF_HAND && player.offhandItem.isEmpty) usedLock.remove(player) // Interact block using off_hand is the last event when off_hand is empty.
            return
        }

        // CustomBlockClickListener
        if (!player.isSecondaryUseActive || player.mainHandItem.isEmpty && player.offhandItem.isEmpty) {
            val packet = event.packet
            val hitResult = packet.hitResult
            val blockEntity = player.level().getBlockEntity(hitResult.blockPos) as? CustomBlockClickListener ?: return
            val result = blockEntity.onClick(player, packet.hand, hitResult)
            if (result.consumesAction()) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(player, hitResult.blockPos, player.getItemInHand(packet.hand))
                event.swing = (result as? InteractionResult.Success)?.swingSource() == InteractionResult.SwingSource.SERVER
                usedLock[player] = player.level().server.tickCount
            }
        }
    }

    @EventListener(true)
    fun onUseItemOnBlock(event: PlayerUseItemOnBlock) {
        val item = event.context.itemInHand
        val player = event.context.player as ServerPlayer

        // Place custom block
        if (CustomBlockHelper.place(BlockPlaceContext(event.context))) {
            event.swing = true
            usedLock[player] = player.level().server.tickCount
            return
        }

        // Crowbar
        if (player.isShiftKeyDown && CustomItemHelper.getItem(item) == Tool.CROWBAR) {
            val world = player.level()
            val blockPos = event.context.clickedPos
            if (world.getBlockEntity(blockPos) !is ModelCustomBlockEntity) return // Check is custom block
            CustomBlockHelper.destroyAndDrop(world, blockPos)
            event.swing = true
            usedLock[player] = world.server.tickCount
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        val player = event.player
        if (usedLock.contains(player)) {
            event.cancel = true
            if (event.packet.hand == InteractionHand.OFF_HAND) usedLock.remove(player) // Interact item is the last interactive event
        }
    }
}
