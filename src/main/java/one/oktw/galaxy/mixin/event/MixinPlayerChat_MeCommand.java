/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MeCommand.class)
public class MixinPlayerChat_MeCommand {
    @Redirect(method = "method_43645", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/registry/RegistryKey;)V",
        ordinal = 0
    ))
    private static void onCommand(PlayerManager playerManager, FilteredMessage<SignedMessage> message, ServerCommandSource source, RegistryKey<MessageType> typeKey) {
        Main main = Main.Companion.getMain();
        ServerPlayerEntity player = source.getPlayer();

        // TODO sync SignedMessage
        if (main == null || player == null || !main.getEventManager().emit(new PlayerChatEvent(player, message.raw().getContent())).getCancel()) {
            playerManager.broadcast(message, source, typeKey);
        } else {
            player.server.logChatMessage(source.getChatMessageSender(), message.raw().getContent().copy().append(" (Canceled)"));
        }
    }
}
