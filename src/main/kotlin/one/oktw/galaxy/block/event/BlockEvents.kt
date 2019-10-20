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
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket
import net.minecraft.text.LiteralText
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ItemType
import one.oktw.galaxy.item.type.ToolType
import java.time.Duration

class BlockEvents {
    private val eventLock: MutableList<PlayerInteractBlockC2SPacket> = mutableListOf()
    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        if (eventLock.contains(event.packet)) return
        eventLock.add(event.packet)
        val world = event.player.serverWorld
        val position = event.packet.hitY.blockPos
        val placePosition = position.offset(event.packet.hitY.side)

        val hand = event.packet.hand

        val itemStack = event.player.getStackInHand(hand)

        if (event.player.isSneaking && itemStack.isItemEqual(Tool(ToolType.WRENCH).createItemStack())) {
            event.player.sendMessage(LiteralText("Break block"))
            waitAndUnlock(event.packet)
            return
        }

        if (!event.player.isSneaking) {
            val entities = world.getEntities(null, Box(position))
            val entity = entities.firstOrNull { entity -> entity.scoreboardTags.contains("BLOCK") }
            if (entity != null) {
                val tag = entity.scoreboardTags.firstOrNull { string -> BlockType.values().map { it.name }.contains(string) }
                if (tag != null) {
                    val blockType = BlockType.valueOf(tag)
                    if (blockType.hasGUI && hand == Hand.MAIN_HAND) openGUI(blockType, event.player)
                    waitAndUnlock(event.packet)
                }
            }
        }

        val tag = itemStack.tag ?: return
        val itemType = tag.getString("customItemType") ?: return
        if (itemType != ItemType.BLOCK.name) return

        val itemBlock = Block(BlockType.valueOf(tag.getString("customBlockType") ?: return))

        GlobalScope.launch {
            itemBlock.activate(world, placePosition)
            waitAndUnlock(event.packet)
        }
    }

    private fun waitAndUnlock(packet: PlayerInteractBlockC2SPacket) {
        GlobalScope.launch {
            delay(Duration.ofSeconds(1))
            eventLock.remove(packet)
        }
    }

    private fun openGUI(blockType: BlockType, player: ServerPlayerEntity) {
        when (blockType) {
            BlockType.CONTROL_PANEL -> player.sendMessage(LiteralText("Control Panel"))
            BlockType.PLANET_TERMINAL -> player.sendMessage(LiteralText("Planet Terminal"))
            BlockType.HT_CRAFTING_TABLE -> player.sendMessage(LiteralText("HTCT"))
            BlockType.TELEPORTER_CORE_BASIC -> player.sendMessage(LiteralText("Basic Teleporter"))
            BlockType.TELEPORTER_CORE_ADVANCE -> player.sendMessage(LiteralText("Advanced Teleporter"))
            else -> Unit
        }
    }
}
