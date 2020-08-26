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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.*
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.mixin.accessor.ServerPlayerEntityFunctionAccessor
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class Spawn : Command {
    private val lock = ConcurrentHashMap.newKeySet<UUID>()

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("spawn")
                .executes { context ->
                    execute(context.source)
                }

        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val player = source.player

        if (player == null || lock.contains(player.uuid)) return com.mojang.brigadier.Command.SINGLE_SUCCESS

        lock += player.uuid

        GlobalScope.launch {
            val world = player.serverWorld

            for (i in 0..4) {
                player.sendMessage(TranslatableText("Respond.commandCountdown", 5 - i).styled { it.withColor(Formatting.GREEN) }, true)
                delay(TimeUnit.SECONDS.toMillis(1))
            }
            player.sendMessage(TranslatableText("Respond.TeleportStart").styled { it.withColor(Formatting.GREEN) }, true)

            withContext(main!!.server.asCoroutineDispatcher()) {
                player.stopRiding()
                if (player.isSleeping) {
                    player.wakeUp(true, true)
                }
                (player as ServerPlayerEntityFunctionAccessor).moveToWorldSpawn(world)
                while (!world.doesNotCollide(player) && player.y < 255) {
                    player.updatePosition(player.x, player.y + 1, player.z)
                }
                player.networkHandler.requestTeleport(player.x, player.y, player.z, player.yaw, player.pitch)
            }
            lock -= player.uuid
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
