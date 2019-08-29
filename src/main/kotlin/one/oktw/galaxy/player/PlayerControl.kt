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

package one.oktw.galaxy.player

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.netty.buffer.Unpooled.wrappedBuffer
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket
import net.minecraft.client.network.packet.CustomPayloadS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import one.oktw.galaxy.Main.Companion.PROXY_IDENTIFIER
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.event.type.PacketReceiveEvent
import one.oktw.galaxy.event.type.PlayerConnectEvent
import one.oktw.galaxy.event.type.RequestCommandCompletionsEvent
import one.oktw.galaxy.proxy.api.ProxyAPI.decode
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.Packet
import one.oktw.galaxy.proxy.api.packet.SearchPlayer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PlayerControl private constructor() {
    companion object {
        fun new() = PlayerControl()
    }

    private var completeID = ConcurrentHashMap<UUID, Int>()
    private var completeInput = ConcurrentHashMap<UUID, String>()
    val startingTarget = ConcurrentHashMap<ServerPlayerEntity, GameProfile>()

    init {
        // Events
        with(main!!) {
            eventManager.register(RequestCommandCompletionsEvent::class, listener = onRequestCommandComplete)
            eventManager.register(PacketReceiveEvent::class, listener = onSearchResult)
            eventManager.register(PlayerConnectEvent::class, listener = onPlayerConnect)
        }
    }

    private val onRequestCommandComplete = fun(event: RequestCommandCompletionsEvent) {
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
    private val onSearchResult = fun(event: PacketReceiveEvent) {
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

    private val onPlayerConnect = fun(event: PlayerConnectEvent) {
        val identifier = Identifier("galaxy:process_${event.player.uuid}")
        val bossBarManager = main!!.server.bossBarManager
        val bossBar = bossBarManager.get(identifier)
        if (bossBar != null) {
            val target = startingTarget[event.player]
            if (target == null) {
                bossBarManager.remove(bossBar)
            } else {
                bossBar.removePlayer(event.player)
                val firstMessage = if (target == event.player) "飛船目前正在飛向您的星系" else "飛船正在飛向 ${target.name} 的星系"
                LiteralText("$firstMessage，重新加入星系以返回航道或更改目的地").styled { style ->
                    style.color = Formatting.YELLOW
                }.let(event.player::sendMessage)
            }
        }
    }
}
