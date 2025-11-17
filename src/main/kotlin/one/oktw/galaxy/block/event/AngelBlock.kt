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
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.phys.BlockHitResult
import one.oktw.galaxy.block.CustomBlock
import one.oktw.galaxy.block.CustomBlockHelper
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerActionEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItemHelper

class AngelBlock {
    private val justBroke = HashSet<ServerPlayer>()
    private val usedLock = HashSet<ServerPlayer>()

    init {
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick {
            justBroke.clear()
            usedLock.clear()
        })
    }

    @EventListener(sync = true)
    fun onPlace(event: PlayerInteractItemEvent) {
        val player = event.player

        if (usedLock.contains(player)) {
            event.cancel = true
            return
        }

        val item = player.getItemInHand(event.packet.hand)
        if (CustomItemHelper.getItem(item) == CustomBlockItem.ANGEL_BLOCK) {
            val blockHit = player.pick(3.0, 1.0f, false) as BlockHitResult
            val placeContext = BlockPlaceContext(player, event.packet.hand, item, blockHit)
            if (CustomBlockHelper.place(placeContext)) event.swing = true

            usedLock.add(player)
        }
    }

    @EventListener(sync = true)
    fun onBreak(event: PlayerActionEvent) {
        val player = event.player
        val blockPos = event.packet.pos
        if (event.packet.action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK &&
            (player.level().getBlockEntity(blockPos) as? CustomBlockEntity)?.getId() == CustomBlock.ANGEL_BLOCK.identifier &&
            !justBroke.contains(player)
        ) {
            CustomBlockHelper.destroyAndDrop(player.level(), blockPos)
            player.level().playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F)
            justBroke.add(player)
        }
    }
}
