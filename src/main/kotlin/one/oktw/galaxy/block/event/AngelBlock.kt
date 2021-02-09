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
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import one.oktw.galaxy.block.CustomBlock
import one.oktw.galaxy.block.CustomBlockHelper
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerActionEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.item.CustomBlockItem

class AngelBlock {
    private val justBreaked = HashSet<ServerPlayerEntity>()
    private val allowReplaceBlocks = arrayOf(Blocks.AIR, Blocks.CAVE_AIR, Blocks.WATER, Blocks.LAVA)

    init {
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick { justBreaked.clear() })
    }

    private fun placeAngelBlock(player: ServerPlayerEntity, hand: Hand) {
        val playerLookVec = player.rotationVector
        val playerPosition = player.pos
        val placePosition = Vec3d(
            playerPosition.x + playerLookVec.x * 3,
            playerPosition.y + playerLookVec.y * 3 + 1.5,
            playerPosition.z + playerLookVec.z * 3
        )
        if (allowReplaceBlocks.contains(player.serverWorld.getBlockState(BlockPos(placePosition)).block)) {
            CustomBlockHelper.place(player.serverWorld, BlockPos(placePosition), CustomBlock.ANGEL_BLOCK)
                .run {
                    if (!player.isCreative) player.setStackInHand(hand, player.getStackInHand(hand).also { it.decrement(1) })
                    player.swingHand(hand)
                }
        }
    }

    @EventListener(sync = true)
    fun onPlace(event: PlayerInteractItemEvent) {
        val player = event.player
        val angelBlockItemStack = CustomBlockItem.ANGEL_BLOCK.createItemStack()
        val mainHandItemStack = player.getStackInHand(Hand.MAIN_HAND)
        val offHandItemStack = player.getStackInHand(Hand.OFF_HAND)
        if (ItemStack.areItemsEqual(angelBlockItemStack, mainHandItemStack) &&
            ItemStack.areTagsEqual(angelBlockItemStack, mainHandItemStack)
        ) {
            placeAngelBlock(player, Hand.MAIN_HAND)
        }
        if (ItemStack.areItemsEqual(angelBlockItemStack, offHandItemStack) &&
            ItemStack.areTagsEqual(angelBlockItemStack, offHandItemStack)
        ) {
            placeAngelBlock(player, Hand.OFF_HAND)
        }
    }

    @EventListener(sync = true)
    fun onBreak(event: PlayerActionEvent) {
        val player = event.player
        val blockPos = event.packet.pos
        if (event.packet.action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK &&
            (player.serverWorld.getBlockEntity(blockPos) as? CustomBlockEntity)?.getId() == CustomBlock.ANGEL_BLOCK.identifier &&
            !justBreaked.contains(player)
        ) {
            CustomBlockHelper.destroy(player.serverWorld, blockPos)
            player.serverWorld.playSound(null, blockPos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
            justBreaked.add(player)
        }
    }
}
