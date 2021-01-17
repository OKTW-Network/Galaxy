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
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.LiteralText
import net.minecraft.util.Hand
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.item.BlockItem
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.util.CustomBlockUtil
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.*
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ItemType
import one.oktw.galaxy.item.type.ToolType

class BlockEvents {
    private val eventLock = HashSet<PlayerInteractBlockC2SPacket>()
    private val usedLock = HashSet<ServerPlayerEntity>()
    private val wrench = Tool(ToolType.WRENCH).createItemStack()

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
        if (tryOpenGUI(event)) {
            player.swingHand(hand, true)
            usedLock.add(player)
            event.cancel = true
        }
    }

    @EventListener(true)
    fun onUseItemOnBlock(event: PlayerUseItemOnBlock) {
        val item = event.context.stack
        val player = event.context.player as ServerPlayerEntity

        // Place custom block
        if (item.item == BlockItem().baseItem && tryPlaceBlock(event.context)) {
            event.swing = true
            usedLock.add(player)
            return
        }

        // Wrench
        if (item.item == Tool().baseItem && player.isSneaking && ItemStack.areEqual(item, wrench)) {
            val world = player.serverWorld
            val blockPos = event.context.blockPos
            CustomBlockUtil.getCustomBlockEntity(world, blockPos) ?: return // Check is custom block
            CustomBlockUtil.removeBlock(world, blockPos)
            event.swing = true
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (usedLock.contains(event.player)) event.cancel = true
    }

    @EventListener(true)
    fun onBlockBreak(event: BlockBreakEvent) {
        CustomBlockUtil.getCustomBlockEntity((event.world as ServerWorld), event.pos) ?: return
        if (event.player?.isCreative == true) {
            CustomBlockUtil.removeBlock(event.world, event.pos, false)
        }
        event.cancel = true
    }

    @EventListener(true)
    fun onBlockExplode(event: BlockExplodeEvent) {
        event.affectedPos.removeIf { CustomBlockUtil.positionIsAnyCustomBlock((event.world as ServerWorld), it) }
    }

    private fun tryOpenGUI(event: PlayerInteractBlockEvent): Boolean {
        val packet = event.packet
        val position = packet.blockHitResult.blockPos
        val hand = packet.hand
        val player = event.player
        val world = player.serverWorld
        if (!player.shouldCancelInteraction()) {
            val entity = CustomBlockUtil.getCustomBlockEntity(world, position) ?: return false
            val blockType = CustomBlockUtil.getTypeFromCustomBlockEntity(entity) ?: return false
            if (blockType.hasGUI && hand == Hand.MAIN_HAND) when (blockType) { // TODO activate GUI
                BlockType.CONTROL_PANEL -> player.sendMessage(LiteralText("Control Panel"), false)
                BlockType.PLANET_TERMINAL -> player.sendMessage(LiteralText("Planet Terminal"), false)
                BlockType.HT_CRAFTING_TABLE -> player.sendMessage(LiteralText("HTCT"), false)
                BlockType.TELEPORTER_CORE_BASIC -> player.sendMessage(LiteralText("Basic Teleporter"), false)
                BlockType.TELEPORTER_CORE_ADVANCE -> player.sendMessage(LiteralText("Advanced Teleporter"), false)
                else -> Unit
            }
            return blockType.hasGUI
        }
        return false
    }

    private fun tryPlaceBlock(context: ItemUsageContext): Boolean {
        val itemStack = context.stack
        if (itemStack.count <= 0) return false

        val tag = itemStack.tag ?: return false
        val itemType = tag.getString("customItemType") ?: return false
        if (itemType != ItemType.BLOCK.name) return false

        val itemBlock = Block(BlockType.valueOf(tag.getString("customBlockType") ?: return false))

        return itemBlock.place(ItemPlacementContext(context))
    }
}
