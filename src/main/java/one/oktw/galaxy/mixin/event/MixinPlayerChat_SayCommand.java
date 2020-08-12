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

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SayCommand.class)
public class MixinPlayerChat_SayCommand {
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
        method = "method_13563",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void onCommand(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir, Text text, TranslatableText translatableText, Entity entity) {
        if (!(entity instanceof ServerPlayerEntity)) return;

        Main main = Main.Companion.getMain();
        ServerPlayerEntity player = (ServerPlayerEntity) entity;

        if (main == null || !main.getEventManager().emit(new PlayerChatEvent(player, translatableText)).getCancel()) {
            player.server.getPlayerManager().broadcastChatMessage(translatableText, MessageType.CHAT, entity.getUuid());
        } else {
            cir.setReturnValue(0);
            cir.cancel();
            player.server.sendSystemMessage(translatableText.append(" (Canceled)"), entity.getUuid());
        }
    }
}
