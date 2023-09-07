/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.mixin.accessor.ServerPlayerEntityFunctionAccessor
import java.util.*
import java.util.concurrent.TimeUnit

class Spawn : Command {
    private val lock = HashSet<UUID>()

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

        main?.launch {
            val world = player.serverWorld

            for (i in 0..4) {
                player.sendMessage(Text.translatable("Respond.commandCountdown", 5 - i).styled { it.withColor(Formatting.GREEN) }, true)
                delay(TimeUnit.SECONDS.toMillis(1))
            }
            player.sendMessage(Text.translatable("Respond.TeleportStart").styled { it.withColor(Formatting.GREEN) }, true)

            val oldPos = player.pos

            player.stopRiding()
            if (player.isSleeping) {
                player.wakeUp(true, true)
            }

            (player as ServerPlayerEntityFunctionAccessor).moveToWorldSpawn(world)
            // force teleport when player pos does not change at all
            if (oldPos.distanceTo(player.pos) == 0.0) {
                val spawnPosition = world.spawnPos
                player.refreshPositionAndAngles(spawnPosition, 0.0f, 0.0f)
            }

            while (!world.isSpaceEmpty(player) && player.y < world.topY) {
                player.updatePosition(player.x, player.y + 1, player.z)
            }
            player.networkHandler.requestTeleport(player.x, player.y, player.z, player.yaw, player.pitch)
            lock -= player.uuid
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
