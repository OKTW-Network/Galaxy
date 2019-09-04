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
import one.oktw.galaxy.proxy.api.ProxyAPI.decode
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.CreateGalaxy
import one.oktw.galaxy.proxy.api.packet.Packet
import one.oktw.galaxy.proxy.api.packet.ProgressStage.*
import java.lang.Math.random
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class Join : Command, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    private val lock = ConcurrentHashMap<ServerPlayerEntity, Mutex>()
    private val queueList = listOf(
        "星系已經排入飛船目的地，請等待系統開始載入星系", // jimchen5209
        "開始自我系統診斷..." // mmis1000
    )
    private val creatingList = listOf(
        "確認星系地形", // jimchen5209
        "載入星系資料庫", // patyhank
        "星系地形資料載入", // patyhank
        "正在嘗試連結至星系", // WoodMan0708
        "載入現實...", //mmis1000
        "正在定位目標星系" // KolinFox
    )
    private val startingList = listOf(
        "選擇目標星系著陸點", // jimchen5209
        "掃描目標區域安全性", // james58899
        "演算最佳航行路線", // james58899
        "正在進行空間跳躍", // james58899
        "正在檢查路線安全性", // patyhank
        "重新定位星系", // WoodMan0708
        "掃描星系周圍區域", // WoodMan0708
        "取得星系內星球位置", // WoodMan0708
        "正在連結至星系", // WoodMan0708
        "選擇最佳決策", // mmis1000
        "正在嘗試路線方案", // mmis1000
        "正在尋找最佳路徑", // KolinFox
        "即將抵達目標星系" // KolinFox
    )

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
        val startingTarget = main!!.playerControl.startingTarget
        val sourcePlayer = source.player
        if (!lock.getOrPut(sourcePlayer, { Mutex() }).tryLock()) {
            val target = startingTarget[sourcePlayer.uuid]
            val message = if (target != null) {
                if (target.id == sourcePlayer.uuid) "飛船目前正在飛向您的星系請稍後" else "飛船正在飛向 ${target.name} 的星系請稍後"
            } else {
                "請稍後..."
            }
            source.sendFeedback(LiteralText(message).styled { style -> style.color = Formatting.AQUA }, false)
            return com.mojang.brigadier.Command.SINGLE_SUCCESS
        }

        val targetPlayer = collection.first()

        sourcePlayer.networkHandler.sendPacket(CustomPayloadS2CPacket(PROXY_IDENTIFIER, PacketByteBuf(wrappedBuffer(encode(CreateGalaxy(targetPlayer.id))))))
        val message = if (startingTarget[sourcePlayer.uuid] != null) {
            if (startingTarget[sourcePlayer.uuid] == targetPlayer) {
                "正在返回航道"
            } else {
                if (sourcePlayer.gameProfile == targetPlayer) "已將目的地更改為您的星系" else "已將目的地更改為 ${targetPlayer.name} 的星系"
            }
        } else {
            if (sourcePlayer.gameProfile == targetPlayer) "已將目的地設為您的星系" else "已將目的地設為 ${targetPlayer.name} 的星系"
        }
        startingTarget.remove(sourcePlayer.uuid)
        val text = LiteralText(message).styled { style ->
            style.color = Formatting.AQUA
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
                        val subText = LiteralText("飛船正在準備起飛...").styled { style -> style.color = Formatting.AQUA }
                        updateVisualStatus(source, text, subText, 0)
                        val tipText = LiteralText(queueList[randomInt(0, queueList.size)]).styled { style ->
                            style.color = Formatting.YELLOW
                        }
                        source.sendFeedback(tipText, false)
                    }
                    Creating -> {
                        val subText = LiteralText("星系載入中...").styled { style -> style.color = Formatting.AQUA }
                        updateVisualStatus(source, text, subText, 10)
                        val tipText = LiteralText(creatingList[randomInt(0, creatingList.size)]).styled { style ->
                            style.color = Formatting.YELLOW
                        }
                        source.sendFeedback(tipText, false)
                    }
                    Starting -> {
                        val subText = LiteralText("飛船正在飛向星系請稍後...").styled { style -> style.color = Formatting.AQUA }
                        updateVisualStatus(source, text, subText, 20)
                        val tipFirstText = LiteralText("飛船正在飛向星系，請耐心等候").styled { style ->
                            style.color = Formatting.YELLOW
                        }
                        source.sendFeedback(tipFirstText, false)
                        startingTarget[sourcePlayer.uuid] = targetPlayer
                        launch {
                            val bossBar = getOrCreateProcessBossBar(source)
                            var tickTime = 100
                            var seconds = 0.0
                            val fastTargetSeconds = 120.0
                            val targetSeconds = 300.0
                            var sentTip = true
                            while (seconds <= targetSeconds) {
                                startingTarget[sourcePlayer.uuid] ?: break
                                bossBar.value += if (bossBar.value > bossBar.maxValue * 0.95) {
                                    0
                                } else if (seconds >= fastTargetSeconds || bossBar.value > bossBar.maxValue * 0.8) {
                                    tickTime = 1000
                                    randomInt(0, 5)
                                } else {
                                    randomInt(5, 10)
                                }
                                delay(Duration.ofMillis(tickTime.toLong()))
                                seconds += tickTime / 1000
                                if ((seconds.toInt() % 20) == 0) {
                                    if (!sentTip) {
                                        val tipText = LiteralText(startingList[randomInt(0, startingList.size)]).styled { style ->
                                            style.color = Formatting.YELLOW
                                        }
                                        source.sendFeedback(tipText, false)
                                        sentTip = true
                                        bossBar.value += randomInt(1, 3)
                                    }
                                } else {
                                    sentTip = false
                                }
                            }
                        }
                    }
                    Started -> {
                        val subText = LiteralText("成功抵達目的地！").styled { style -> style.color = Formatting.GREEN }
                        sourcePlayer.sendMessage(subText)
                        val bossBar = getOrCreateProcessBossBar(source)
                        updateVisualStatus(source, text, subText, bossBar.maxValue)
                        startingTarget.remove(sourcePlayer.uuid)
                        lock[sourcePlayer]?.unlock()
                        lock.remove(sourcePlayer)
                        launch {
                            delay(Duration.ofSeconds(2))
                            removeProcessBossBar(source)
                        }
                    }
                    Failed -> {
                        val subText = LiteralText("您的飛船在飛行途中炸毀了，請聯絡開發團隊！").styled { style -> style.color = Formatting.RED }
                        sourcePlayer.sendMessage(subText)
                        updateVisualStatus(source, text, subText, 0)
                        startingTarget.remove(sourcePlayer.uuid)
                        lock[sourcePlayer]?.unlock()
                        lock.remove(sourcePlayer)
                        launch {
                            delay(Duration.ofSeconds(2))
                            removeProcessBossBar(source)
                        }
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

    private fun randomInt(min: Int, max: Int): Int = (random() * ((max - min) - 1)).toInt() + min

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
        val identifier = Identifier("galaxy:process_${player.uuid}")
        val bossBarManager = source.minecraftServer.bossBarManager
        val bossBar = bossBarManager.get(identifier)
        if (bossBar != null) {
            bossBar.clearPlayers()
            bossBarManager.remove(bossBar)
        }
    }

    private fun getOrCreateProcessBossBar(source: ServerCommandSource): CommandBossBar {
        val player = source.player
        val identifier = Identifier("galaxy:process_${player.uuid}")
        val bossBarManager = source.minecraftServer.bossBarManager

        val bossBar = bossBarManager.get(identifier)
        return if (bossBar == null) {
            val newBossBar = bossBarManager.add(identifier, LiteralText("請稍後..."))
            newBossBar.color = BossBar.Color.BLUE
            newBossBar.isVisible = true
            newBossBar.maxValue = 1000
            newBossBar.addPlayer(player)
            newBossBar
        } else {
            bossBar.addPlayer(player)
            bossBar
        }
    }

    private fun sendTitle(player: ServerPlayerEntity, title: Text, subTitle: Text) {
        player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.TITLE, title))
        player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.SUBTITLE, subTitle))
    }
}
