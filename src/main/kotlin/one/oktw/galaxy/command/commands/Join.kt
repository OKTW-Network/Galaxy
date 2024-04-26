/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2024
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
import com.mojang.brigadier.suggestion.Suggestions
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.command.argument.GameProfileArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.event.type.ProxyResponseEvent
import one.oktw.galaxy.network.ProxyAPIPayload
import one.oktw.galaxy.proxy.api.packet.CreateGalaxy
import one.oktw.galaxy.proxy.api.packet.ProgressStage.*
import one.oktw.galaxy.proxy.api.packet.SearchPlayer
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class Join : Command, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    private val lock = ConcurrentHashMap<ServerPlayerEntity, Mutex>()

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("join")
                .executes { context -> execute(context.source, listOf(context.source.playerOrThrow.gameProfile)) }
                .then(
                    CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                        .suggests { commandContext, suggestionsBuilder ->
                            val future = CompletableFuture<Suggestions>()
                            val player = commandContext.source.playerOrThrow

                            ServerPlayNetworking.send(player, ProxyAPIPayload(SearchPlayer(suggestionsBuilder.remaining, 10)))

                            val listeners = fun(event: ProxyResponseEvent) {
                                val result = event.packet as? SearchPlayer.Result ?: return
                                if (event.player.uuid != player.uuid) return

                                result.players.forEach(suggestionsBuilder::suggest)
                                future.complete(suggestionsBuilder.build())
                            }

                            val eventManager = main!!.eventManager

                            eventManager.register(ProxyResponseEvent::class, listeners)
                            future.thenRun { eventManager.unregister(ProxyResponseEvent::class, listeners) } // TODO check leak

                            return@suggests future
                        }
                        .executes { context ->
                            execute(context.source, GameProfileArgumentType.getProfileArgument(context, "player"))
                        }
                )
        )
    }

    private fun execute(source: ServerCommandSource, collection: Collection<GameProfile>): Int {
        val sourcePlayer = source.playerOrThrow
        if (!lock.getOrPut(sourcePlayer) { Mutex() }.tryLock()) {
            source.sendFeedback({ Text.of("請稍後...") }, false)
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val targetPlayer = collection.first()

        ServerPlayNetworking.send(sourcePlayer, ProxyAPIPayload(CreateGalaxy(targetPlayer.id)))
        source.sendFeedback({ Text.of(if (sourcePlayer.gameProfile == targetPlayer) "正在加入您的星系" else "正在加入 ${targetPlayer.name} 的星系") }, false)

        launch {

            val listener = fun(event: ProxyResponseEvent) {
                if (event.player.gameProfile != sourcePlayer.gameProfile) return

                val data = event.packet as? CreateGalaxy.CreateProgress ?: return

                if (data.uuid != targetPlayer.id) return

                when (data.stage) {
                    Queue -> sourcePlayer.sendMessage(Text.of("正在等待星系載入"), false)
                    Creating -> sourcePlayer.sendMessage(Text.of("星系載入中..."), false)
                    Starting -> sourcePlayer.sendMessage(Text.of("星系正在啟動請稍後..."), false)
                    Started -> {
                        sourcePlayer.sendMessage(Text.of("星系已載入！"), false)
                        lock[sourcePlayer]?.unlock()
                        lock.remove(sourcePlayer)
                    }

                    Failed -> {
                        sourcePlayer.sendMessage(Text.of("星系載入失敗，請聯絡開發團隊！"), false)
                        lock[source.player]?.unlock()
                        lock.remove(sourcePlayer)
                    }
                }
            }

            main!!.eventManager.register(ProxyResponseEvent::class, listener)
            delay(Duration.ofMinutes(5).toMillis()) // TODO change to kotlin Duration
            main!!.eventManager.unregister(ProxyResponseEvent::class, listener)
            lock[sourcePlayer]?.unlock()
            lock.remove(sourcePlayer)
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
