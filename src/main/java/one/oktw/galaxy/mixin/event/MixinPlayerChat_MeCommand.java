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

package one.oktw.galaxy.mixin.event;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import one.oktw.galaxy.event.EventManager;
import one.oktw.galaxy.event.type.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(MeCommand.class)
public class MixinPlayerChat_MeCommand {
    @Redirect(method = "method_43645", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/network/message/MessageType$Parameters;)V",
        ordinal = 0
    ))
    private static void onCommand(PlayerManager playerManager, SignedMessage message, ServerCommandSource source, MessageType.Parameters messageType) {
        ServerPlayerEntity player = source.getPlayer();

        // TODO sync SignedMessage
        if (player == null || !EventManager.safeEmit(new PlayerChatEvent(player, Text.translatable("chat.type.emote", player.getDisplayName(), message.getContent()))).getCancel()) {
            playerManager.broadcast(message, source, messageType);
        } else {
            Objects.requireNonNull(player.getServer()).logChatMessage(message.getContent(), messageType, "Canceled");
        }
    }
}
