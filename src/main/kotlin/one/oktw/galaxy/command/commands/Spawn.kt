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

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import java.util.*
import java.util.concurrent.TimeUnit

class Spawn : Command {
    private val lock = HashSet<UUID>()

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("spawn")
                .executes { context ->
                    execute(context.source)
                    SINGLE_SUCCESS
                }

        )
    }

    private fun execute(source: CommandSourceStack) {
        val originPlayer = source.player

        if (originPlayer == null || lock.contains(originPlayer.uuid)) return

        lock += originPlayer.uuid

        main?.launch {
            for (i in 0..4) {
                originPlayer.displayClientMessage(
                    Component.translatable("Respond.commandCountdown", 5 - i).withStyle { it.withColor(ChatFormatting.GREEN) },
                    true
                )
                delay(TimeUnit.SECONDS.toMillis(1))
            }

            val player = originPlayer.level().server.playerList.getPlayer(originPlayer.uuid)
            if (player == null) {
                lock -= originPlayer.uuid
                return@launch
            }
            player.displayClientMessage(Component.translatable("Respond.TeleportStart").withStyle { it.withColor(ChatFormatting.GREEN) }, true)

            val world = player.level()
            val type = world.dimension()

            if (type == Level.NETHER) {
                player.displayClientMessage(Component.translatable("Respond.TeleportNothing").withStyle { it.withColor(ChatFormatting.RED) }, true)
                lock -= player.uuid
                return@launch
            }

            val oldPos = player.position()

            player.stopRiding()
            if (player.isSleeping) {
                player.stopSleepInBed(true, true)
            }

            if (type == Level.END) {
                val pos = ServerLevel.END_SPAWN_POINT.center
                player.connection.teleport(pos.x, pos.y, pos.z, 0.0f, 0.0f)
                player.connection.resetPosition()
                lock -= player.uuid
                return@launch
            }

            player.snapTo(player.adjustSpawnLocation(world, world.respawnData.pos()).bottomCenter, 0.0f, 0.0f)
            // force teleport when player pos does not change at all
            if (oldPos.distanceTo(player.position()) == 0.0) {
                player.snapTo(world.respawnData.pos(), 0.0f, 0.0f)
            }

            while (!world.noCollision(player) && player.y < world.maxY) {
                player.absSnapTo(player.x, player.y + 1, player.z)
            }
            player.connection.teleport(player.x, player.y, player.z, player.yRot, player.xRot)
            lock -= player.uuid
        }
    }
}
