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

package one.oktw.galaxy.network

import io.netty.buffer.Unpooled.wrappedBuffer
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import one.oktw.galaxy.proxy.api.ProxyAPI
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.Packet

@JvmRecord
data class ProxyChatPayload(val packet: Packet) : CustomPayload {
    companion object {
        private val PROXY_CHAT_IDENTIFIER = Identifier("galaxy", "proxy-chat")
        val ID = CustomPayload.Id<ProxyChatPayload>(PROXY_CHAT_IDENTIFIER)
        val CODEC: PacketCodec<PacketByteBuf, ProxyChatPayload> = PacketCodec.of(
            { value, buf -> buf.writeBytes(wrappedBuffer(encode(value.packet))) },
            { buf -> ProxyChatPayload(ProxyAPI.decode(buf.nioBuffer())) })
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> {
        return ID
    }
}
