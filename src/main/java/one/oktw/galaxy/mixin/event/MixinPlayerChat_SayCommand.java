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

import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SayCommand.class)
public class MixinPlayerChat_SayCommand {
    @Inject(
        method = "method_43657",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/registry/RegistryKey;)V",
            ordinal = 0
        ),
        cancellable = true
    )
    private static void onCommand(PlayerManager playerManager, ServerCommandSource serverCommandSource, FilteredMessage<SignedMessage> decoratedMessage, CallbackInfo ci) {
        if (!serverCommandSource.isExecutedByPlayer()) return;

        Main main = Main.Companion.getMain();
        ServerPlayerEntity player = serverCommandSource.getPlayer();

        // TODO sync SignedMessage
        if (main != null) {
            assert player != null;
            if (main.getEventManager().emit(new PlayerChatEvent(player, Text.translatable("chat.type.announcement", player.getDisplayName(), decoratedMessage.raw().getContent()))).getCancel()) {
                ci.cancel();
                player.server.logChatMessage(player.asMessageSender(), decoratedMessage.raw().getContent().copy().append(" (Canceled)"));
            }
        }
    }
}
