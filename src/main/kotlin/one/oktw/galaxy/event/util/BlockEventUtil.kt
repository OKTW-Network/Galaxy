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

package one.oktw.galaxy.event.util

import net.minecraft.block.Block
import net.minecraft.client.network.packet.BlockUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

object BlockEventUtil {
    fun updateBlockAndInventory(player: ServerPlayerEntity, world: ServerWorld, blockPos: BlockPos) {
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(1, 0, 0)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, 0, 1)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(-1, 0, 0)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, 0, -1)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, 1, 0)))
        player.networkHandler.sendPacket(BlockUpdateS2CPacket(world, blockPos.add(0, -1, 0)))
        player.onContainerRegistered(player.container, player.container.stacks)

    }

    fun isNextTo(world: ServerWorld, blockPos: BlockPos, block: Block): Boolean {
        return world.getBlockState(blockPos.add(1, 0, 0)).block == block ||
            world.getBlockState(blockPos.add(0, 0, 1)).block == block ||
            world.getBlockState(blockPos.add(-1, 0, 0)).block == block ||
            world.getBlockState(blockPos.add(0, 0, -1)).block == block
    }
}
