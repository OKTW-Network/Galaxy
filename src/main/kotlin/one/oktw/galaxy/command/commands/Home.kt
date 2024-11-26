/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2024
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
import net.minecraft.block.Blocks
import net.minecraft.block.RespawnAnchorBlock
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.TeleportTarget
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import java.util.*
import java.util.concurrent.TimeUnit

class Home : Command {
    private val lock = HashSet<UUID>()

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

        // Check Stage
        val spawnPointPosition = player.spawnPointPosition
        if (spawnPointPosition == null) {
            player.sendMessage(Text.translatable("block.minecraft.spawn.not_valid").styled { it.withColor(Formatting.RED) }, false)
            lock -= player.uuid
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val teleportTarget = player.getRespawnTarget(player.notInAnyWorld, TeleportTarget.NO_OP)
        if (teleportTarget.missingRespawnBlock()) {
            player.sendMessage(Text.translatable("block.minecraft.spawn.not_valid").styled { it.withColor(Formatting.RED) }, false)
            lock -= player.uuid
        } else {
            main?.launch {
                for (i in 0..4) {
                    player.sendMessage(Text.translatable("Respond.commandCountdown", 5 - i).styled { it.withColor(Formatting.GREEN) }, true)
                    delay(TimeUnit.SECONDS.toMillis(1))
                }
                player.sendMessage(Text.translatable("Respond.TeleportStart").styled { it.withColor(Formatting.GREEN) }, true)

                // Check Again (Actual Teleport Stage)
                val realTeleportTarget = player.getRespawnTarget(player.notInAnyWorld, TeleportTarget.NO_OP)
                if (realTeleportTarget.missingRespawnBlock()) {
                    player.sendMessage(Text.translatable("block.minecraft.spawn.not_valid").styled { it.withColor(Formatting.RED) }, false)
                    lock -= player.uuid
                    return@launch
                }

                player.teleportTo(realTeleportTarget)

                val realSpawnPointPosition = player.spawnPointPosition
                val realWorld = source.server.getWorld(player.spawnPointDimension)
                if (realWorld != null && realSpawnPointPosition != null) {
                    val blockState = realWorld.getBlockState(realSpawnPointPosition)
                    if (!player.notInAnyWorld && blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                        // Consume Respawn Anchor Charge
                        realWorld.setBlockState(
                            realSpawnPointPosition, blockState.with(RespawnAnchorBlock.CHARGES, blockState[RespawnAnchorBlock.CHARGES] - 1)
                        )
                        realWorld.updateNeighbors(realSpawnPointPosition, blockState.block)

                        player.networkHandler.sendPacket(
                            PlaySoundS2CPacket(
                                SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE,
                                SoundCategory.BLOCKS,
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
