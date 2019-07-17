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

package one.oktw.galaxy.chat

import io.netty.buffer.Unpooled
import net.minecraft.client.network.packet.CustomPayloadS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import one.oktw.galaxy.Main
import one.oktw.galaxy.event.type.PlayerChatEvent
import one.oktw.galaxy.proxy.api.ProxyAPI
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.MessageSend

class Exchange {
    companion object {
        val PROXY_CHAT_IDENTIFIER = Identifier("galaxy", "proxy/chat")
    }

    init {
        Main.main?.eventManager?.register(PlayerChatEvent::class, true, ::handleChat)
    }

    fun handleChat(event: PlayerChatEvent) {
        event.cancel = true

        event.player.networkHandler.sendPacket(
            CustomPayloadS2CPacket(
                PROXY_CHAT_IDENTIFIER, PacketByteBuf(
                    Unpooled.wrappedBuffer(
                        encode(
                            MessageSend(
                                sender = event.player.uuid,
                                message = Text.Serializer.toJson(event.message),
                                targets = listOf(ProxyAPI.globalChatChannel)
                            )
                        )
                    )
                )
            )
        )
    }
}
