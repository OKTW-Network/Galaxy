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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.ChatFormat
import net.minecraft.client.network.packet.TitleS2CPacket
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import one.oktw.galaxy.command.Command
import java.util.concurrent.TimeUnit

class Spawn : Command {
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
        GlobalScope.launch {
            for (i in 0..4) {
                val component = TextComponent("請等待 ${5 - i} 秒鐘").modifyStyle { style -> style.color = ChatFormat.GREEN }
                player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, component))
                delay(TimeUnit.SECONDS.toMillis(1))
            }
            // TODO (Broken spawnPos)
            player.setPositionAndAngles(source.world.spawnPos, 0.0F, 0.0F)
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
