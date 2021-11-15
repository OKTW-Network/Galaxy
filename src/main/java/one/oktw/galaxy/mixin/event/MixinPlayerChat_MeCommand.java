/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Function;

@Mixin(MeCommand.class)
public class MixinPlayerChat_MeCommand {
    @Redirect(method = "method_31375", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V",
        ordinal = 0
    ))
    private static void onCommand(PlayerManager playerManager, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType playerMessageType, UUID sender) {
        Main main = Main.Companion.getMain();
        ServerPlayerEntity player = playerManager.getPlayer(sender);

        if (main == null || player == null || !main.getEventManager().emit(new PlayerChatEvent(player, serverMessage)).getCancel()) {
            playerManager.broadcast(serverMessage, playerMessageFactory, playerMessageType, sender);
        } else {
            player.server.sendSystemMessage(((BaseText) serverMessage).append(" (Canceled)"), sender);
        }
    }
}
