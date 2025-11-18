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

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.level.ServerPlayer;
import one.oktw.galaxy.event.EventManager;
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
            target = "Lnet/minecraft/server/players/PlayerList;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            ordinal = 0
        ),
        cancellable = true
    )
    private static void onCommand(CommandContext<CommandSourceStack> context, PlayerChatMessage message, CallbackInfo ci) {
        CommandSourceStack serverCommandSource = context.getSource();
        if (!serverCommandSource.isPlayer()) return;

        ServerPlayer player = serverCommandSource.getPlayer();

        // TODO sync SignedMessage
        assert player != null;
        if (EventManager.safeEmit(new PlayerChatEvent(player, Component.translatable("chat.type.announcement", player.getDisplayName(), message.decoratedContent()))).getCancel()) {
            ci.cancel();
            player.level().getServer().logChatMessage(message.decoratedContent(), ChatType.bind(ChatType.SAY_COMMAND, serverCommandSource), "Canceled");
        }
    }
}
