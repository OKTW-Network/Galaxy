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

import com.google.common.collect.Lists
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.arguments.GameProfileArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import one.oktw.galaxy.command.Command

class Join : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("join")
                .executes { context ->
                    val profile = Lists.newArrayList<GameProfile>()
                    profile.add(context.source.player.gameProfile)
                    execute(context.source, profile)
                }
                .then(
                    CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
                        //用來移除 ＠ 開頭的自動完成
                        .suggests { context, suggestionsBuilder ->
                            context.source.minecraftServer.playerManager.playerList
                                .forEach { suggestionsBuilder.suggest(it.name.asString()) }
                            return@suggests suggestionsBuilder.buildFuture()
                        }
                        .executes { context ->
                            execute(context.source, GameProfileArgumentType.getProfileArgument(context, "targets"))
                        }
                )
        )
    }

    private fun execute(source: ServerCommandSource, collection: Collection<GameProfile>): Int {
        // TODO (戳Proxy加入玩家）
        val players = collection.iterator()

        while (players.hasNext()) {
            val player = players.next()
            source.sendFeedback(LiteralText("已傳入玩家 ${player.name} UUID: ${player.id}"), false)
            break
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
