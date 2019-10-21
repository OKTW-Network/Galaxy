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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import net.minecraft.block.Blocks
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket
import net.minecraft.text.LiteralText
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import net.minecraft.world.GameMode
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.util.BlockUtil
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ItemType
import one.oktw.galaxy.item.type.ToolType
import java.time.Duration

class BlockEvents {
    private val eventLock: MutableList<PlayerInteractBlockC2SPacket> = mutableListOf()
    private val mainHandUsedLock: MutableList<ServerPlayerEntity> = mutableListOf()
    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        if (eventLock.contains(event.packet) || mainHandUsedLock.contains(event.player)) return
        eventLock.add(event.packet)
        if (tryBreakBlock(event.packet, event.player, event.packet.hand)) return
        if (tryOpenGUI(event)) return
        waitAndUnlock(event.packet)
        tryPlaceBlock(event.packet, event.player)
    }

    private fun setMainHandUsed(player: ServerPlayerEntity) {
        GlobalScope.launch {
            mainHandUsedLock.add(player)
            delay(Duration.ofMillis(50))
            mainHandUsedLock.remove(player)
        }
    }

    private fun waitAndUnlock(packet: PlayerInteractBlockC2SPacket) {
        GlobalScope.launch {
            delay(Duration.ofSeconds(1))
            eventLock.remove(packet)
        }
    }

    private fun tryBreakBlock(packet: PlayerInteractBlockC2SPacket, player: ServerPlayerEntity, hand: Hand): Boolean {
        val world = player.serverWorld
        val position = packet.hitY.blockPos
        if (world.getBlockState(position).block != Blocks.BARRIER) return false
        if (player.isSneaking && player.getStackInHand(hand).isItemEqual(Tool(ToolType.WRENCH).createItemStack())) {
            BlockUtil.detectBlock(world, position) ?: return false
            BlockUtil.removeBlock(world, position)
            waitAndUnlock(packet)
            if (hand == Hand.MAIN_HAND) setMainHandUsed(player)
            return true
        }
        return false
    }

    private fun tryOpenGUI(event: PlayerInteractBlockEvent): Boolean {
        val world = event.player.serverWorld
        val position = event.packet.hitY.blockPos
        val hand = event.packet.hand
        if (!event.player.isSneaking) {
            val entity = BlockUtil.detectBlock(world, position) ?: return false
            val blockType = BlockUtil.getTypeFromBlock(entity) ?: return false
            if (blockType.hasGUI && hand == Hand.MAIN_HAND) openGUI(blockType, event.player, event)
            waitAndUnlock(event.packet)
            if (hand == Hand.MAIN_HAND) setMainHandUsed(event.player)
            return true
        }
        return false
    }

    private fun openGUI(blockType: BlockType, player: ServerPlayerEntity, event: PlayerInteractBlockEvent) {
        event.cancel = true
        when (blockType) {
            BlockType.CONTROL_PANEL -> player.sendMessage(LiteralText("Control Panel"))
            BlockType.PLANET_TERMINAL -> player.sendMessage(LiteralText("Planet Terminal"))
            BlockType.HT_CRAFTING_TABLE -> player.sendMessage(LiteralText("HTCT"))
            BlockType.TELEPORTER_CORE_BASIC -> player.sendMessage(LiteralText("Basic Teleporter"))
            BlockType.TELEPORTER_CORE_ADVANCE -> player.sendMessage(LiteralText("Advanced Teleporter"))
            else -> Unit
        }
    }

    private fun tryPlaceBlock(packet: PlayerInteractBlockC2SPacket, player: ServerPlayerEntity) {
        val world = player.serverWorld
        val server = player.server
        val position = packet.hitY.blockPos
        val placePosition = position.offset(packet.hitY.side)

        val hand = packet.hand

        val itemStack = player.getStackInHand(hand)

        val tag = itemStack.tag ?: return
        val itemType = tag.getString("customItemType") ?: return
        if (itemType != ItemType.BLOCK.name) return

        val itemBlock = Block(BlockType.valueOf(tag.getString("customBlockType") ?: return))

        // server world height check
        if (position.y < server.worldHeight - 1 || packet.hitY.side != Direction.UP && position.y < server.worldHeight) {
            // player modifiable world check
            if (world.canPlayerModifyAt(player, placePosition) && player.interactionManager.gameMode != GameMode.SPECTATOR) {
                GlobalScope.launch {
                    if (hand == Hand.MAIN_HAND) setMainHandUsed(player)

                    val success = itemBlock.activate(world, placePosition)

                    if (success) {
                        if (player.interactionManager.gameMode != GameMode.CREATIVE) {
                            itemStack.decrement(1)
                            player.setStackInHand(hand, itemStack)
                        }
                    } else if (hand == Hand.MAIN_HAND) { // if place failed try fire again offhand action (Because it was cancelled before)
                        tryBreakBlock(packet, player, Hand.OFF_HAND)
                    }
                }
            }
        }
    }
}
