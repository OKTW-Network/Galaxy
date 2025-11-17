/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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
import com.mojang.brigadier.suggestion.Suggestions
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.NameAndId
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
    private val lock = ConcurrentHashMap<ServerPlayer, Mutex>()

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("join")
                .executes { context -> execute(context.source, listOf(NameAndId(context.source.playerOrException.gameProfile))) }
                .then(
                    Commands.argument("player", GameProfileArgument.gameProfile())
                        .suggests { commandContext, suggestionsBuilder ->
                            val future = CompletableFuture<Suggestions>()
                            val player = commandContext.source.playerOrException

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
                            execute(context.source, GameProfileArgument.getGameProfiles(context, "player"))
                        }
                )
        )
    }

    private fun execute(source: CommandSourceStack, collection: Collection<NameAndId>): Int {
        val sourcePlayer = source.playerOrException
        if (!lock.getOrPut(sourcePlayer) { Mutex() }.tryLock()) {
            source.sendSuccess({ Component.nullToEmpty("請稍後...") }, false)
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val targetPlayer = collection.first()

        ServerPlayNetworking.send(sourcePlayer, ProxyAPIPayload(CreateGalaxy(targetPlayer.id)))
        source.sendSuccess(
            { Component.nullToEmpty(if (sourcePlayer.gameProfile == targetPlayer) "正在加入您的星系" else "正在加入 ${targetPlayer.name} 的星系") },
            false
        )

        launch {

            val listener = fun(event: ProxyResponseEvent) {
                if (event.player.gameProfile != sourcePlayer.gameProfile) return

                val data = event.packet as? CreateGalaxy.CreateProgress ?: return

                if (data.uuid != targetPlayer.id) return

                when (data.stage) {
                    Queue -> sourcePlayer.displayClientMessage(Component.nullToEmpty("正在等待星系載入"), false)
                    Creating -> sourcePlayer.displayClientMessage(Component.nullToEmpty("星系載入中..."), false)
                    Starting -> sourcePlayer.displayClientMessage(Component.nullToEmpty("星系正在啟動請稍後..."), false)
                    Started -> {
                        sourcePlayer.displayClientMessage(Component.nullToEmpty("星系已載入！"), false)
                        lock[sourcePlayer]?.unlock()
                        lock.remove(sourcePlayer)
                    }

                    Failed -> {
                        sourcePlayer.displayClientMessage(Component.nullToEmpty("星系載入失敗，請聯絡開發團隊！"), false)
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
