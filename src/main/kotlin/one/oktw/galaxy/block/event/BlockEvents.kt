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
import net.minecraft.item.ItemPlacementContext
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
import one.oktw.galaxy.event.type.BlockBreakEvent
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock
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
    fun onUseItem(event: PlayerUseItemOnBlock) {
        val item = event.context.stack
        val player = event.context.player as ServerPlayerEntity
        if (item.item == BlockItem().baseItem) {
            if (player.isCreativeLevelTwoOp) return
            if (tryPlaceBlock(event.context)) {
                player.swingHand(event.context.hand, true)
                usedLock.add(player)
            }
        }
        if (item.item == Tool().baseItem) {
            if (tryBreakBlock(event.context)) {
                if (player.getStackInHand(Hand.MAIN_HAND).isItemEqual(Tool(ToolType.WRENCH).createItemStack())) {
                    player.swingHand(Hand.MAIN_HAND, true)
                } else {
                    player.swingHand(event.context.hand, true)
                }
                usedLock.add(player)
            }
        }
    }

    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        if (usedLock.contains(event.player)) event.cancel = true
    }

    @EventListener(true)
    fun onBlockBreak(event: BlockBreakEvent) {
        CustomBlockUtil.getCustomBlockEntity((event.world as ServerWorld), event.pos) ?: return
        event.cancel = true
    }

    private fun tryBreakBlock(context: ItemUsageContext): Boolean {
        val world = context.world as ServerWorld
        val position = context.blockPos
        val player = context.player as ServerPlayerEntity
        if (world.getBlockState(position).block != Blocks.BARRIER) return false
        if (player.shouldCancelInteraction() && context.stack.isItemEqual(Tool(ToolType.WRENCH).createItemStack())) {
            CustomBlockUtil.getCustomBlockEntity(world, position) ?: return false
            CustomBlockUtil.removeBlock(world, position)
            return true
        }
        return false
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
            if (blockType.hasGUI && hand == Hand.MAIN_HAND) openGUI(blockType, player)
            return blockType.hasGUI
        }
        return false
    }

    private fun openGUI(blockType: BlockType, player: ServerPlayerEntity) {
        when (blockType) { // TODO activate GUI
            BlockType.CONTROL_PANEL -> player.sendMessage(LiteralText("Control Panel"), false)
            BlockType.PLANET_TERMINAL -> player.sendMessage(LiteralText("Planet Terminal"), false)
            BlockType.HT_CRAFTING_TABLE -> player.sendMessage(LiteralText("HTCT"), false)
            BlockType.TELEPORTER_CORE_BASIC -> player.sendMessage(LiteralText("Basic Teleporter"), false)
            BlockType.TELEPORTER_CORE_ADVANCE -> player.sendMessage(LiteralText("Advanced Teleporter"), false)
            else -> Unit
        }
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
