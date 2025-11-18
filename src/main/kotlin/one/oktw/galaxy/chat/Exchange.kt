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

package one.oktw.galaxy.chat

import com.google.gson.GsonBuilder
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.createS2CPacket
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.ComponentSerialization
import one.oktw.galaxy.Main
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerChatEvent
import one.oktw.galaxy.network.ProxyChatPayload
import one.oktw.galaxy.proxy.api.ProxyAPI
import one.oktw.galaxy.proxy.api.packet.MessageSend

class Exchange {
    @EventListener(true)
    fun handleChat(event: PlayerChatEvent) {
        if (Main.selfUUID == ProxyAPI.dummyUUID) return

        val gson = GsonBuilder().disableHtmlEscaping().create()
        val registries = RegistryAccess.EMPTY

        event.cancel = true

        event.player.connection.send(
            createS2CPacket(
                ProxyChatPayload(
                    MessageSend(
                        sender = event.player.uuid,
                        message = gson.toJson(
                            ComponentSerialization.CODEC
                                .encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), event.message)
                                .orThrow
                        ),
                        targets = listOf(ProxyAPI.globalChatChannel)
                    )
                )
            )
        )
    }
}
