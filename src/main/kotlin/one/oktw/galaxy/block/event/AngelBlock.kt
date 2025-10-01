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
import net.minecraft.block.Blocks
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import one.oktw.galaxy.block.CustomBlock
import one.oktw.galaxy.block.CustomBlockHelper
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerActionEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItemHelper

class AngelBlock {
    private val justBroke = HashSet<ServerPlayerEntity>()
    private val usedLock = HashSet<ServerPlayerEntity>()
    private val allowReplaceBlocks = arrayOf(Blocks.AIR, Blocks.CAVE_AIR, Blocks.WATER, Blocks.LAVA)

    init {
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick {
            justBroke.clear()
            usedLock.clear()
        })
    }

    private fun placeAngelBlock(player: ServerPlayerEntity, hand: Hand, item: ItemStack) {
        val blockHit = player.raycast(3.0, 1.0f, false) as BlockHitResult
        if (allowReplaceBlocks.contains(player.world.getBlockState(blockHit.blockPos).block)) {
            val placeContext = ItemPlacementContext(player, hand, item, blockHit)
            CustomBlockHelper.place(placeContext)
                .run {
                    if (!player.isCreative) player.setStackInHand(hand, player.getStackInHand(hand).also { it.decrement(1) })
                }
        }
    }

    @EventListener(sync = true)
    fun onPlace(event: PlayerInteractItemEvent) {
        val player = event.player

        if (usedLock.contains(player)) {
            event.cancel = true
            return
        }

        val item = player.getStackInHand(event.packet.hand)
        if (CustomItemHelper.getItem(item) == CustomBlockItem.ANGEL_BLOCK) {
            placeAngelBlock(player, event.packet.hand, item)
            event.swing = true
            usedLock.add(player)
        }
    }

    @EventListener(sync = true)
    fun onBreak(event: PlayerActionEvent) {
        val player = event.player
        val blockPos = event.packet.pos
        if (event.packet.action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK &&
            (player.world.getBlockEntity(blockPos) as? CustomBlockEntity)?.getId() == CustomBlock.ANGEL_BLOCK.identifier &&
            !justBroke.contains(player)
        ) {
            CustomBlockHelper.destroyAndDrop(player.world.toServerWorld(), blockPos)
            player.world.toServerWorld().playSound(null, blockPos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
            justBroke.add(player)
        }
    }
}
