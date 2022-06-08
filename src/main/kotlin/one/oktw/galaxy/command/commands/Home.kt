/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.*
import net.minecraft.block.RespawnAnchorBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import one.oktw.galaxy.command.Command
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class Home : Command {

    private val lock = ConcurrentHashMap.newKeySet<UUID>()

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("home")
                .executes { context ->
                    execute(context.source)
                }
        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val player = source.player

        if (player == null || lock.contains(player.uuid)) return com.mojang.brigadier.Command.SINGLE_SUCCESS

        lock += player.uuid

        val spawnPointPosition = player.spawnPointPosition
        if (spawnPointPosition == null) {
            player.sendMessage(Text.translatable("block.minecraft.spawn.not_valid").styled { it.withColor(Formatting.RED) }, false)
            lock -= player.uuid
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val world = source.server.getWorld(player.spawnPointDimension)

        val spawnPoint = PlayerEntity.findRespawnPosition(
            world,
            spawnPointPosition,
            player.spawnAngle,
            player.isSpawnForced,
            player.notInAnyWorld
        )
        if (!spawnPoint.isPresent) {
            player.sendMessage(Text.translatable("block.minecraft.spawn.not_valid").styled { it.withColor(Formatting.RED) }, false)
            lock -= player.uuid
        } else {
            GlobalScope.launch {
                // Add back charge in countdown stage
                if (world != null) {
                    withContext(player.server.asCoroutineDispatcher()) {
                        val blockState = world.getBlockState(spawnPointPosition)
                        if (blockState.block is RespawnAnchorBlock) {
                            world.setBlockState(
                                spawnPointPosition, blockState.with(RespawnAnchorBlock.CHARGES, blockState[RespawnAnchorBlock.CHARGES] + 1)
                            )
                            world.updateNeighbors(spawnPointPosition, blockState.block)
                        }
                    }
                }

                for (i in 0..4) {
                    player.sendMessage(Text.translatable("Respond.commandCountdown", 5 - i).styled { it.withColor(Formatting.GREEN) }, true)
                    delay(TimeUnit.SECONDS.toMillis(1))
                }
                player.sendMessage(Text.translatable("Respond.TeleportStart").styled { it.withColor(Formatting.GREEN) }, true)

                withContext(player.server.asCoroutineDispatcher()) {
                    // Check Again
                    val checkAgain = PlayerEntity.findRespawnPosition(
                        world,
                        spawnPointPosition,
                        player.spawnAngle,
                        player.isSpawnForced,
                        player.notInAnyWorld
                    )
                    if (!checkAgain.isPresent) {
                        player.sendMessage(Text.translatable("block.minecraft.spawn.not_valid").styled { it.withColor(Formatting.RED) }, false)
                        lock -= player.uuid
                        return@withContext
                    }

                    val world2 = if (world != null && checkAgain.isPresent) world else source.server.overworld
                    val position = checkAgain.get()
                    player.teleport(
                        world2,
                        position.x,
                        position.y,
                        position.z,
                        player.yaw,
                        player.pitch
                    )

                    val blockState = world2.getBlockState(spawnPointPosition)
                    if (!player.notInAnyWorld && blockState.block is RespawnAnchorBlock) {
                        world2.playSound(
                            null, spawnPointPosition, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 1.0F, 1.0F
                        )
                    }
                }
                lock -= player.uuid
            }
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
