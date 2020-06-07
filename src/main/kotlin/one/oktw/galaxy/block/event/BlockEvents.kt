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
import one.oktw.galaxy.block.util.CustomBlockUtil
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.BlockBreakEvent
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ItemType
import one.oktw.galaxy.item.type.ToolType
import one.oktw.galaxy.player.util.BlockUtil

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
        if (event.cancel) return

        val player = event.player
        val hand = event.packet.hand
        val hitResult = event.packet.hitY
        val blockPos = hitResult.blockPos

        val tryUseBlock = CustomBlockUtil.vanillaTryUseBlock(player, hand, hitResult)
        if (tryUseBlock.isAccepted) {
            event.cancel = true
            if (tryUseBlock.shouldSwingHand()) player.swingHand(hand, true)
            return
        }

        if (BlockUtil.isMature(player.serverWorld, blockPos, player.serverWorld.getBlockState(blockPos))) return

        if (eventLock.contains(event.packet) || usedLock.contains(player)) return
        eventLock.add(event.packet)

        var finished: Boolean
        finished = tryBreakBlock(event.packet, player, hand)
        if (!finished) finished = tryOpenGUI(event)
        if (!finished) finished = tryPlaceBlock(event.packet, player)
        if (finished) {
            player.swingHand(hand, true)
            usedLock.add(player)
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
        CustomBlockUtil.getCustomBlockEntity((event.world as ServerWorld), event.pos) ?: return
        event.cancel = true
    }

    private fun tryBreakBlock(packet: PlayerInteractBlockC2SPacket, player: ServerPlayerEntity, hand: Hand): Boolean {
        val world = player.serverWorld
        val position = packet.hitY.blockPos
        if (world.getBlockState(position).block != Blocks.BARRIER) return false
        if (player.isSneaking && player.getStackInHand(hand).isItemEqual(Tool(ToolType.WRENCH).createItemStack())) {
            CustomBlockUtil.getCustomBlockEntity(world, position) ?: return false
            CustomBlockUtil.removeBlock(world, position)
            return true
        }
        return false
    }

    private fun tryOpenGUI(event: PlayerInteractBlockEvent): Boolean {
        val player = event.player
        val world = player.serverWorld
        val position = event.packet.hitY.blockPos
        val hand = event.packet.hand
        if (!player.shouldCancelInteraction()) {
            val entity = CustomBlockUtil.getCustomBlockEntity(world, position) ?: return false
            val blockType = CustomBlockUtil.getTypeFromCustomBlockEntity(entity) ?: return false
            if (blockType.hasGUI && hand == Hand.MAIN_HAND) openGUI(blockType, player)
            return blockType.hasGUI
        }
        return false
    }

    private fun openGUI(blockType: BlockType, player: ServerPlayerEntity) {
        when (blockType) { // TODO activate GUI
            BlockType.CONTROL_PANEL -> player.sendMessage(LiteralText("Control Panel"))
            BlockType.PLANET_TERMINAL -> player.sendMessage(LiteralText("Planet Terminal"))
            BlockType.HT_CRAFTING_TABLE -> player.sendMessage(LiteralText("HTCT"))
            BlockType.TELEPORTER_CORE_BASIC -> player.sendMessage(LiteralText("Basic Teleporter"))
            BlockType.TELEPORTER_CORE_ADVANCE -> player.sendMessage(LiteralText("Advanced Teleporter"))
            else -> Unit
        }
    }

    private fun tryPlaceBlock(packet: PlayerInteractBlockC2SPacket, player: ServerPlayerEntity): Boolean {
        val world = player.serverWorld
        val server = player.server
        val position = packet.hitY.blockPos
        val placePosition = CustomBlockUtil.getPlacePosition(world, position, packet.hitY)

        val hand = packet.hand

        val itemStack = player.getStackInHand(hand)

        if (itemStack.count <= 0) return false

        val tag = itemStack.tag ?: return false
        val itemType = tag.getString("customItemType") ?: return false
        if (itemType != ItemType.BLOCK.name) return false

        val itemBlock = Block(BlockType.valueOf(tag.getString("customBlockType") ?: return false))

        // server world height check
        if (position.y < server.worldHeight - 1 || packet.hitY.side != Direction.UP && position.y < server.worldHeight) {
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
