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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level.OVERWORLD
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RespawnAnchorBlock
import net.minecraft.world.level.portal.TeleportTransition
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import java.util.*
import java.util.concurrent.TimeUnit

class Home : Command {
    private val lock = HashSet<UUID>()

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("home")
                .executes { context ->
                    execute(context.source)
                }
        )
    }

    private fun execute(source: CommandSourceStack): Int {
        val player = source.player

        if (player == null || lock.contains(player.uuid)) return com.mojang.brigadier.Command.SINGLE_SUCCESS

        lock += player.uuid

        // Check Stage
        val spawnPointPosition = player.respawnConfig?.respawnData?.pos()
        if (spawnPointPosition == null) {
            player.displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid").withStyle { it.withColor(ChatFormatting.RED) }, false)
            lock -= player.uuid
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val teleportTarget = player.findRespawnPositionAndUseSpawnBlock(player.wonGame, TeleportTransition.DO_NOTHING)
        if (teleportTarget.missingRespawnBlock()) {
            player.displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid").withStyle { it.withColor(ChatFormatting.RED) }, false)
            lock -= player.uuid
        } else {
            main?.launch {
                for (i in 0..4) {
                    player.displayClientMessage(
                        Component.translatable("Respond.commandCountdown", 5 - i).withStyle { it.withColor(ChatFormatting.GREEN) },
                        true
                    )
                    delay(TimeUnit.SECONDS.toMillis(1))
                }
                player.displayClientMessage(Component.translatable("Respond.TeleportStart").withStyle { it.withColor(ChatFormatting.GREEN) }, true)

                // Check Again (Actual Teleport Stage)
                val realTeleportTarget = player.findRespawnPositionAndUseSpawnBlock(player.wonGame, TeleportTransition.DO_NOTHING)
                if (realTeleportTarget.missingRespawnBlock()) {
                    player.displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid").withStyle { it.withColor(ChatFormatting.RED) }, false)
                    lock -= player.uuid
                    return@launch
                }

                player.teleport(realTeleportTarget)

                val realSpawnPointPosition = player.respawnConfig?.respawnData?.pos()
                val realWorld = source.server.getLevel(player.respawnConfig?.respawnData?.dimension() ?: OVERWORLD)
                if (realWorld != null && realSpawnPointPosition != null) {
                    val blockState = realWorld.getBlockState(realSpawnPointPosition)
                    if (!player.wonGame && blockState.`is`(Blocks.RESPAWN_ANCHOR)) {
                        // Consume Respawn Anchor Charge
                        realWorld.setBlockAndUpdate(
                            realSpawnPointPosition, blockState.setValue(RespawnAnchorBlock.CHARGE, blockState.getValue(RespawnAnchorBlock.CHARGE) - 1)
                        )
                        realWorld.updateNeighborsAt(realSpawnPointPosition, blockState.block)

                        player.connection.send(
                            ClientboundSoundPacket(
                                SoundEvents.RESPAWN_ANCHOR_DEPLETE,
                                SoundSource.BLOCKS,
                                realSpawnPointPosition.x.toDouble(),
                                realSpawnPointPosition.y.toDouble(),
                                realSpawnPointPosition.z.toDouble(),
                                1.0f,
                                1.0f,
                                realWorld.getRandom().nextLong()
                            )
                        )
                    }

                    lock -= player.uuid
                }
            }
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
