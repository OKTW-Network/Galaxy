/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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
import net.minecraft.text.Text;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(MeCommand.class)
public class MixinPlayerChat_MeCommand {
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "method_13238", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V",
        ordinal = 0
    ))
    private static void onCommand(PlayerManager playerManager, Text message, MessageType type, UUID senderUuid) {
        Main main = Main.Companion.getMain();
        ServerPlayerEntity player = playerManager.getPlayer(senderUuid);

        if (main == null || player == null || !main.getEventManager().emit(new PlayerChatEvent(player, message)).getCancel()) {
            playerManager.broadcastChatMessage(message, type, senderUuid);
        }
    }
}
