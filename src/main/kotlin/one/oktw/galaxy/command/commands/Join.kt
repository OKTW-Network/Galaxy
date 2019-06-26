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
import net.minecraft.command.arguments.EntityArgumentType
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import one.oktw.galaxy.command.Command

class Join : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("join")
                .executes { context ->
                    execute(context.source, context.source.player)
                }
                .then(
                    CommandManager.argument("targets", EntityArgumentType.player())
                        .executes { context ->
                            execute(context.source, EntityArgumentType.getPlayer(context, "targets"))
                        }
                )
        )
    }

    private fun execute(source: ServerCommandSource, player: ServerPlayerEntity): Int {
        // TODO (戳Proxy加入玩家）
        source.sendFeedback(TextComponent("已傳入玩家 ${player.displayName.formattedText}"), false)
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
