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
import net.minecraft.client.network.packet.TitleS2CPacket
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import java.util.concurrent.TimeUnit

class Home : Command {
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
        GlobalScope.launch {
            if(source.player.spawnPosition == null){
                val component = LiteralText("找不到您的家 :(").styled { style -> style.color = Formatting.GREEN }
            }else {
                for (i in 0..4) {
                    val component = LiteralText("請等待 ${5 - i} 秒鐘").styled { style -> style.color = Formatting.GREEN }
                    player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, component))
                    delay(TimeUnit.SECONDS.toMillis(1))
                }
                withContext(main!!.server.asCoroutineDispatcher()) {
                    player.setPositionAndAngles(source.player.spawnPosition, 0.0F, 0.0F)
                }
            }
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
