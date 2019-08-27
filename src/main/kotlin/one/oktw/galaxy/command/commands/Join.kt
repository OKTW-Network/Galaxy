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
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.netty.buffer.Unpooled.wrappedBuffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.time.delay
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket
import net.minecraft.client.network.packet.CustomPayloadS2CPacket
import net.minecraft.client.network.packet.TitleS2CPacket
import net.minecraft.command.arguments.GameProfileArgumentType
import net.minecraft.entity.boss.BossBar
import net.minecraft.entity.boss.CommandBossBar
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import one.oktw.galaxy.Main.Companion.PROXY_IDENTIFIER
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.event.type.PacketReceiveEvent
import one.oktw.galaxy.event.type.RequestCommandCompletionsEvent
import one.oktw.galaxy.proxy.api.ProxyAPI.decode
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.CreateGalaxy
import one.oktw.galaxy.proxy.api.packet.Packet
import one.oktw.galaxy.proxy.api.packet.ProgressStage.*
import one.oktw.galaxy.proxy.api.packet.ProgressStage.Queue
import one.oktw.galaxy.proxy.api.packet.SearchPlayer
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Join : Command, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    private val lock = ConcurrentHashMap<ServerPlayerEntity, Mutex>()
    private val starting = ConcurrentHashMap<ServerPlayerEntity, Boolean>()

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

    companion object {
        private var completeID = ConcurrentHashMap<UUID, Int>()
        private var completeInput = ConcurrentHashMap<UUID, String>()

        fun registerEvent() {
            val requestCompletionListener = fun(event: RequestCommandCompletionsEvent) {
                val command = event.packet.partialCommand

                //取消原版自動完成並向 proxy 發請求
                if (command.startsWith("/join ")) {
                    event.cancel = true
                    completeID[event.player.uuid] = event.packet.completionId
                    completeInput[event.player.uuid] = command
                    event.player.networkHandler.sendPacket(
                        CustomPayloadS2CPacket(
                            PROXY_IDENTIFIER,
                            PacketByteBuf(wrappedBuffer(encode(SearchPlayer(command.removePrefix("/join "), 10))))
                        )
                    )
                }
            }

            //從 proxy 接收回覆並送自動完成封包給玩家
            val searchResultListener = fun(event: PacketReceiveEvent) {
                if (event.channel != PROXY_IDENTIFIER) return
                val data = decode<Packet>(event.packet.nioBuffer()) as? SearchPlayer.Result ?: return
                val id = completeID[event.player.uuid] ?: return
                val input = completeInput[event.player.uuid] ?: return

                val suggestion = SuggestionsBuilder(input, "/join ".length)
                data.players.forEach { player ->
                    suggestion.suggest(player)
                }

                event.player.networkHandler.sendPacket(
                    CommandSuggestionsS2CPacket(
                        id,
                        suggestion.buildFuture().get()
                    )
                )
            }

            main!!.eventManager.register(RequestCommandCompletionsEvent::class, listener = requestCompletionListener)
            main!!.eventManager.register(PacketReceiveEvent::class, listener = searchResultListener)
        }
    }

    private fun execute(source: ServerCommandSource, collection: Collection<GameProfile>): Int {
        val sourcePlayer = source.player
        if (!lock.getOrPut(sourcePlayer, { Mutex() }).tryLock()) {
            source.sendFeedback(LiteralText("請稍後...").styled { style -> style.color = Formatting.YELLOW }, false)
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val targetPlayer = collection.first()

        sourcePlayer.networkHandler.sendPacket(CustomPayloadS2CPacket(PROXY_IDENTIFIER, PacketByteBuf(wrappedBuffer(encode(CreateGalaxy(targetPlayer.id))))))
        val text = LiteralText(if (sourcePlayer.gameProfile == targetPlayer) "正在加入您的星系" else "正在加入 ${targetPlayer.name} 的星系").styled { style ->
            style.color = Formatting.YELLOW
        }
        source.sendFeedback(text, false)
        sourcePlayer.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.TITLE, text))

        launch {
            val listener = fun(event: PacketReceiveEvent) {
                if (event.player.gameProfile != sourcePlayer.gameProfile || event.channel != PROXY_IDENTIFIER) return

                val data = decode<Packet>(event.packet.nioBuffer()) as? CreateGalaxy.CreateProgress ?: return

                if (data.uuid != targetPlayer.id) return

                when (data.stage) {
                    Queue -> {
                        val subText = LiteralText("正在等待星系載入").styled { style -> style.color = Formatting.YELLOW }
                        updateVisualStatus(source, text, subText, 0)
                    }
                    Creating -> {
                        val subText = LiteralText("星系載入中...").styled { style -> style.color = Formatting.YELLOW }
                        updateVisualStatus(source, text, subText, 20)
                    }
                    Starting -> {
                        val subText = LiteralText("星系正在啟動請稍後...").styled { style -> style.color = Formatting.YELLOW }
                        updateVisualStatus(source, text, subText, 40)
                        starting[sourcePlayer] = true
                        println("Outside launch ${starting[sourcePlayer]}")
                        launch {
                            val bossBar = getOrCreateProcessBossBar(source)
                            var seconds = 0.0
                            val fastTargetSeconds = 120.0
                            val targetSeconds = 300.0
                            while (true) {
                                val starting = starting[sourcePlayer] ?: false
                                if (!starting || seconds >= targetSeconds) {
                                    break
                                }
                                bossBar.value = if (seconds <= fastTargetSeconds) {
                                    40 + (130 * (seconds / fastTargetSeconds)).toInt()
                                } else {
                                    170 + (29 * ((seconds - fastTargetSeconds) / (targetSeconds - fastTargetSeconds))).toInt()
                                }
                                delay(Duration.ofMillis(500))
                                seconds += 0.5
                                if (seconds >= 1) {
                                    LiteralText("星系正在啟動請稍後... 已經過 ${seconds.toInt()} 秒").styled { style ->
                                        style.color = Formatting.YELLOW
                                    }.let(bossBar::setName)
                                }
                            }
                        }
                    }
                    Started -> {
                        val subText = LiteralText("星系已載入！").styled { style -> style.color = Formatting.GREEN }
                        sourcePlayer.sendMessage(subText)
                        updateVisualStatus(source, text, subText, 200)
                        starting[sourcePlayer] = false
                        starting.remove(sourcePlayer)
                        lock[sourcePlayer]?.unlock()
                        lock.remove(sourcePlayer)
                        launch {
                            delay(Duration.ofSeconds(2))
                            removeProcessBossBar(source)
                        }
                    }
                    Failed -> {
                        sourcePlayer.sendMessage(LiteralText("星系載入失敗，請聯絡開發團隊！").styled { style -> style.color = Formatting.RED })
                        starting[sourcePlayer] = false
                        starting.remove(sourcePlayer)
                        removeProcessBossBar(source)
                        lock[sourcePlayer]?.unlock()
                        lock.remove(sourcePlayer)
                    }
                }
            }

            main!!.eventManager.register(PacketReceiveEvent::class, listener = listener)
            delay(Duration.ofMinutes(5))
            main!!.eventManager.unregister(listener)
            starting[sourcePlayer] = false
            starting.remove(sourcePlayer)
            removeProcessBossBar(source)
            lock[sourcePlayer]?.unlock()
            lock.remove(sourcePlayer)
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }

    private fun updateVisualStatus(source: ServerCommandSource, title: Text, subTitle: Text, progress: Int) {
        // bossBar
        val bossBar = getOrCreateProcessBossBar(source)
        bossBar.name = subTitle
        bossBar.value = progress

        // title
        sendTitle(source.player, title, subTitle)
    }

    private fun removeProcessBossBar(source: ServerCommandSource) {
        val player = source.player
        val identifier = Identifier("process_${player.uuid}")
        val bossBarManager = source.minecraftServer.bossBarManager
        val bossBar = bossBarManager.get(identifier)
        if (bossBar != null) {
            bossBarManager.remove(bossBar)
        }
    }

    private fun getOrCreateProcessBossBar(source: ServerCommandSource): CommandBossBar {
        val player = source.player
        val identifier = Identifier("process_${player.uuid}")
        val bossBarManager = source.minecraftServer.bossBarManager

        val bossBar = bossBarManager.get(identifier)
        return if (bossBar == null) {
            val newBossBar = bossBarManager.add(identifier, LiteralText("請稍後..."))
            newBossBar.color = BossBar.Color.YELLOW
            newBossBar.isVisible = true
            newBossBar.addPlayer(player)
            newBossBar
        } else {
            bossBar
        }
    }

    private fun sendTitle(player: ServerPlayerEntity, title: Text, subTitle: Text) {
        player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.TITLE, title))
        player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.SUBTITLE, subTitle))
    }
}
