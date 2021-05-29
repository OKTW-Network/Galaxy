/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import one.oktw.galaxy.block.CustomBlockHelper
import one.oktw.galaxy.block.entity.ModelCustomBlockEntity
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock
import one.oktw.galaxy.item.CustomItemHelper
import one.oktw.galaxy.item.Tool

class BlockEvents {
    private val eventLock = HashSet<PlayerInteractBlockC2SPacket>()
    private val usedLock = HashSet<ServerPlayerEntity>()

    init {
        ServerTickEvents.END_WORLD_TICK.register(
            ServerTickEvents.EndWorldTick {
                eventLock.clear()
                usedLock.clear()
            }
        )
    }

    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val packet = event.packet
        val hand = packet.hand
        val player = event.player
        if (eventLock.contains(packet) || usedLock.contains(player)) {
            event.cancel = true
            return
        }
        eventLock.add(packet)
        // TODO GUI
//        if (tryOpenGUI(event)) {
//            player.swingHand(hand, true)
//            usedLock.add(player)
//            event.cancel = true
//        }
    }

    @EventListener(true)
    fun onUseItemOnBlock(event: PlayerUseItemOnBlock) {
        val item = event.context.stack
        val player = event.context.player as ServerPlayerEntity

        // Place custom block
        if (CustomBlockHelper.place(ItemPlacementContext(event.context))) {
            event.swing = true
            usedLock.add(player)
            return
        }

        // Wrench
        if (player.isSneaking && CustomItemHelper.getItem(item) == Tool.WRENCH) {
            val world = player.serverWorld
            val blockPos = event.context.blockPos
            if (world.getBlockEntity(blockPos) !is ModelCustomBlockEntity) return // Check is custom block
            CustomBlockHelper.destroyAndDrop(world, blockPos)
            event.swing = true
            usedLock.add(player)
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (usedLock.contains(event.player)) event.cancel = true
    }

    // TODO GUI
//    private fun tryOpenGUI(event: PlayerInteractBlockEvent): Boolean {
//        val packet = event.packet
//        val position = packet.blockHitResult.blockPos
//        val hand = packet.hand
//        val player = event.player
//        val world = player.serverWorld
//        if (!player.shouldCancelInteraction()) {
//            val entity = CustomBlockUtil.getCustomBlockEntity(world, position) ?: return false
//            val blockType = CustomBlockUtil.getTypeFromCustomBlockEntity(entity) ?: return false
//            if (blockType.hasGUI && hand == Hand.MAIN_HAND) when (blockType) { // TODO activate GUI
//                BlockType.CONTROL_PANEL -> player.sendMessage(LiteralText("Control Panel"), false)
//                BlockType.PLANET_TERMINAL -> player.sendMessage(LiteralText("Planet Terminal"), false)
//                BlockType.HT_CRAFTING_TABLE -> player.sendMessage(LiteralText("HTCT"), false)
//                BlockType.TELEPORTER_CORE_BASIC -> player.sendMessage(LiteralText("Basic Teleporter"), false)
//                BlockType.TELEPORTER_CORE_ADVANCE -> player.sendMessage(LiteralText("Advanced Teleporter"), false)
//                else -> Unit
//            }
//            return blockType.hasGUI
//        }
//        return false
//    }
}
