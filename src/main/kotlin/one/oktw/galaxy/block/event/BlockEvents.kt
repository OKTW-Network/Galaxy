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

package one.oktw.galaxy.block.event

import net.fabricmc.fabric.api.event.server.ServerTickCallback
import net.minecraft.block.Blocks
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.LiteralText
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import net.minecraft.world.GameMode
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.util.BlockUtil
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.BlockBreakEvent
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ItemType
import one.oktw.galaxy.item.type.ToolType

class BlockEvents {
    private val eventLock = HashSet<PlayerInteractBlockC2SPacket>()
    private val usedLock = HashSet<ServerPlayerEntity>()

    init {
        ServerTickCallback.EVENT.register(
            ServerTickCallback {
                eventLock.clear()
                usedLock.clear()
            }
        )
    }

    @Suppress("unused")
    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        if (eventLock.contains(event.packet) || usedLock.contains(event.player)) return
        eventLock.add(event.packet)

        var finished: Boolean
        finished = tryBreakBlock(event.packet, event.player, event.packet.hand)
        if (!finished) finished = tryOpenGUI(event)
        if (!finished) finished = tryPlaceBlock(event.packet, event.player)
        if (finished) {
            event.player.swingHand(event.packet.hand, true)
            usedLock.add(event.player)
            event.cancel = true
        }
    }

    @Suppress("DuplicatedCode", "unused")
    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (usedLock.contains(event.player)) event.cancel = true
    }

    @Suppress("UNUSED_PARAMETER", "unused")
    @EventListener(true)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.state.block != Blocks.BARRIER) return
        BlockUtil.getCustomBlockEntity((event.world as ServerWorld), event.pos) ?: return
        event.cancel = true
    }

    private fun tryBreakBlock(packet: PlayerInteractBlockC2SPacket, player: ServerPlayerEntity, hand: Hand): Boolean {
        val world = player.serverWorld
        val position = packet.blockHitResult.blockPos
        if (world.getBlockState(position).block != Blocks.BARRIER) return false
        if (player.isSneaking && player.getStackInHand(hand).isItemEqual(Tool(ToolType.WRENCH).createItemStack())) {
            BlockUtil.getCustomBlockEntity(world, position) ?: return false
            BlockUtil.removeBlock(world, position)
            return true
        }
        return false
    }

    private fun tryOpenGUI(event: PlayerInteractBlockEvent): Boolean {
        val world = event.player.serverWorld
        val position = event.packet.blockHitResult.blockPos
        val hand = event.packet.hand
        if (!event.player.isSneaking) {
            val entity = BlockUtil.getCustomBlockEntity(world, position) ?: return false
            val blockType = BlockUtil.getTypeFromCustomBlockEntity(entity) ?: return false
            if (blockType.hasGUI && hand == Hand.MAIN_HAND) openGUI(blockType, event.player, event)
            return blockType.hasGUI
        }
        return false
    }

    private fun openGUI(blockType: BlockType, player: ServerPlayerEntity, event: PlayerInteractBlockEvent) {
        event.cancel = true
        when (blockType) { // TODO activate GUI
            BlockType.CONTROL_PANEL -> player.sendMessage(LiteralText("Control Panel"), false)
            BlockType.PLANET_TERMINAL -> player.sendMessage(LiteralText("Planet Terminal"), false)
            BlockType.HT_CRAFTING_TABLE -> player.sendMessage(LiteralText("HTCT"), false)
            BlockType.TELEPORTER_CORE_BASIC -> player.sendMessage(LiteralText("Basic Teleporter"), false)
            BlockType.TELEPORTER_CORE_ADVANCE -> player.sendMessage(LiteralText("Advanced Teleporter"), false)
            else -> Unit
        }
    }

    private fun tryPlaceBlock(packet: PlayerInteractBlockC2SPacket, player: ServerPlayerEntity): Boolean {
        val world = player.serverWorld
        val server = player.server
        val position = packet.blockHitResult.blockPos
        val placePosition = BlockUtil.getPlacePosition(world, position, packet.blockHitResult)

        val hand = packet.hand

        val itemStack = player.getStackInHand(hand)

        if (itemStack.count <= 0) return false

        val tag = itemStack.tag ?: return false
        val itemType = tag.getString("customItemType") ?: return false
        if (itemType != ItemType.BLOCK.name) return false

        val itemBlock = Block(BlockType.valueOf(tag.getString("customBlockType") ?: return false))

        // server world height check
        if (position.y < server.worldHeight - 1 || packet.blockHitResult.side != Direction.UP && position.y < server.worldHeight) {
            // player modifiable world check
            if (world.canPlayerModifyAt(player, placePosition) && player.interactionManager.gameMode != GameMode.SPECTATOR) {
                val success = itemBlock.activate(world, placePosition)

                if (success) {
                    if (player.interactionManager.gameMode != GameMode.CREATIVE) {
                        itemStack.decrement(1)
                        player.setStackInHand(hand, itemStack)
                    }
                    return true
                } else if (hand == Hand.MAIN_HAND) { // if place failed try fire again offhand action (Because it was cancelled before)
                    return tryBreakBlock(packet, player, Hand.OFF_HAND)
                }
            }
        }
        return false
    }
}
