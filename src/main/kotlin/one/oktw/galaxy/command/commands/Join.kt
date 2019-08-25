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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.time.delay
import net.minecraft.client.network.packet.CustomPayloadS2CPacket
import net.minecraft.command.arguments.GameProfileArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import net.minecraft.util.PacketByteBuf
import one.oktw.galaxy.Main.Companion.PROXY_IDENTIFIER
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.event.type.PacketReceiveEvent
import one.oktw.galaxy.proxy.api.ProxyAPI.decode
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.CreateGalaxy
import one.oktw.galaxy.proxy.api.packet.Packet
import one.oktw.galaxy.proxy.api.packet.ProgressStage.*
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class Join : Command, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    private val lock = ConcurrentHashMap<ServerPlayerEntity, Mutex>()

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("join")
                .executes { context ->
                    execute(context.source, listOf(context.source.player.gameProfile))
                }
                .then(
                    CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                        .suggests { _, suggestionsBuilder ->
                            //先給個空的 Suggest
                            return@suggests suggestionsBuilder.buildFuture()
                        }
                        .executes { context ->
                            execute(context.source, GameProfileArgumentType.getProfileArgument(context, "player"))
                        }
                )
        )
    }

    private fun execute(source: ServerCommandSource, collection: Collection<GameProfile>): Int {
        if (!lock.getOrPut(source.player, { Mutex() }).tryLock()) {
            source.sendFeedback(LiteralText("請稍後...").styled { style -> style.color = Formatting.YELLOW }, false)
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val targetPlayer = collection.first()

        source.player.networkHandler.sendPacket(CustomPayloadS2CPacket(PROXY_IDENTIFIER, PacketByteBuf(wrappedBuffer(encode(CreateGalaxy(targetPlayer.id))))))
        val text = LiteralText(if (source.player.gameProfile == targetPlayer) "正在加入您的星系" else "正在加入 ${targetPlayer.name} 的星系").styled { style ->
            style.color = Formatting.YELLOW
        }
        source.sendFeedback(text, false)

        launch {
            val sourcePlayer = source.player

            val listener = fun(event: PacketReceiveEvent) {
                if (event.player.gameProfile != sourcePlayer.gameProfile || event.channel != PROXY_IDENTIFIER) return

                val data = decode<Packet>(event.packet.nioBuffer()) as? CreateGalaxy.CreateProgress ?: return

                if (data.uuid != targetPlayer.id) return

                when (data.stage) {
                    Queue -> sourcePlayer.sendMessage(LiteralText("正在等待星系載入").styled { style -> style.color = Formatting.YELLOW })
                    Creating -> sourcePlayer.sendMessage(LiteralText("星系載入中...").styled { style -> style.color = Formatting.YELLOW })
                    Starting -> sourcePlayer.sendMessage(LiteralText("星系正在啟動請稍後...").styled { style -> style.color = Formatting.YELLOW })
                    Started -> {
                        sourcePlayer.sendMessage(LiteralText("星系已載入！").styled { style -> style.color = Formatting.GREEN })
                        lock[sourcePlayer]?.unlock()
                        lock.remove(sourcePlayer)
                    }
                    Failed -> {
                        sourcePlayer.sendMessage(LiteralText("星系載入失敗，請聯絡開發團隊！").styled { style -> style.color = Formatting.RED })
                        lock[source.player]?.unlock()
                        lock.remove(sourcePlayer)
                    }
                }
            }

            main!!.eventManager.register(PacketReceiveEvent::class, listener = listener)
            delay(Duration.ofMinutes(5))
            main!!.eventManager.unregister(listener)
            lock[sourcePlayer]?.unlock()
            lock.remove(sourcePlayer)
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
