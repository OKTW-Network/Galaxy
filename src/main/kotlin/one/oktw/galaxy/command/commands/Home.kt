/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
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

        if (source !is ServerPlayerEntity || lock.contains(source.player.uuid)) return com.mojang.brigadier.Command.SINGLE_SUCCESS

        lock += source.player.uuid

        if (source.player.spawnPosition == null) {
            player.sendMessage(LiteralText("找不到您的家").styled { style -> style.color = Formatting.RED })
        } else {
            GlobalScope.launch {
                for (i in 0..4) {
                    val component = LiteralText("請等待 ${5 - i} 秒鐘").styled { style -> style.color = Formatting.GREEN }
                    player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, component))
                    delay(TimeUnit.SECONDS.toMillis(1))
                }
                withContext(player.server.asCoroutineDispatcher()) {
                    player.requestTeleport(source.player.spawnPosition.x.toDouble(),source.player.spawnPosition.y.toDouble(),source.player.spawnPosition.z.toDouble())
                }
                lock -= source.player.uuid
            }
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
