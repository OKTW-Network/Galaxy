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

package one.oktw.galaxy.network

import io.netty.buffer.Unpooled.wrappedBuffer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import one.oktw.galaxy.proxy.api.ProxyAPI
import one.oktw.galaxy.proxy.api.ProxyAPI.encode
import one.oktw.galaxy.proxy.api.packet.Packet

@JvmRecord
data class ProxyAPIPayload(val packet: Packet) : CustomPacketPayload {
    companion object {
        private val PROXY_IDENTIFIER = Identifier.fromNamespaceAndPath("galaxy", "proxy")
        val ID = CustomPacketPayload.Type<ProxyAPIPayload>(PROXY_IDENTIFIER)
        val CODEC: StreamCodec<FriendlyByteBuf, ProxyAPIPayload> = StreamCodec.ofMember(
            { value, buf -> buf.writeBytes(wrappedBuffer(encode(value.packet))) },
            { buf ->
                val packet = ProxyAPIPayload(ProxyAPI.decode(buf.nioBuffer()))
                // FIXME: Workaround force clear buffer to suppress "Packet was larger than I expected" error
                buf.clear()
                return@ofMember packet
            })
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }
}
