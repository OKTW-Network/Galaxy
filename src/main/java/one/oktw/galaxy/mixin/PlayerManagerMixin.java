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

package one.oktw.galaxy.mixin;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import one.oktw.galaxy.proxy.api.ProxyAPI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {
    @Final
    @Shadow()
    private final List<ServerPlayerEntity> players = Lists.newArrayList();

    private static final Identifier GALAXY_CHAT = new Identifier("galaxy/chat");
    @Inject(
        method = "broadcastChatMessage(Lnet/minecraft/network/chat/Component;Z)V",
        at = @At("HEAD")
    )
    public void onBroadcastChatMessage(Component component_1, boolean isSystemMessage, CallbackInfo ci) {
        if (isSystemMessage) return;
        ci.cancel();

        ByteBuf content = Unpooled.buffer();
        PacketByteBuf buff = new PacketByteBuf(content);
        buff.writeBytes(ProxyAPI.INSTANCE.encode(TextComponent.Serializer.toJson(component_1)));

        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(GALAXY_CHAT, buff);

        if (players.size() > 0) {
            ServerPlayerEntity player = players.get(0);

            player.networkHandler.sendPacket(packet);
        }
    }
}
