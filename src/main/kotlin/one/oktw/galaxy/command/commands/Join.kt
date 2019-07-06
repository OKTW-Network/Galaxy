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

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import io.netty.buffer.Unpooled.wrappedBuffer
import net.minecraft.command.arguments.GameProfileArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.packet.CustomPayloadC2SPacket
import net.minecraft.text.LiteralText
import net.minecraft.util.PacketByteBuf
import one.oktw.galaxy.Main.Companion.PROXY_IDENTIFIER
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.CreateGalaxy

class Join : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("join")
                .executes { context ->
                    val profile = listOf(context.source.player.gameProfile)
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
        val player = collection.first()

        source.player.networkHandler.sendPacket(
            CustomPayloadC2SPacket(PROXY_IDENTIFIER, PacketByteBuf(wrappedBuffer(encode(CreateGalaxy(player.id)))))
        )
        source.sendFeedback(LiteralText(if (source.player.gameProfile == player) "正在加入您的星系" else "正在加入 ${player.name} 的星系"), false)

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
