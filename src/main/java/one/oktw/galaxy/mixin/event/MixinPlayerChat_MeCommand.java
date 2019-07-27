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

package one.oktw.galaxy.mixin.event;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MeCommand.class)
public class MixinPlayerChat_MeCommand {
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "method_13238(Lcom/mojang/brigadier/context/CommandContext;)I", at = @At(value = "INVOKE", target = "net/minecraft/server/PlayerManager.sendToAll(Lnet/minecraft/text/Text;)V"))
    private static void onCommand(PlayerManager playerManager, Text message, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Main main = Main.Companion.getMain();
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (main == null || !main.getEventManager().emit(new PlayerChatEvent(player, message)).getCancel()) {
            playerManager.sendToAll(message);
        } else {
            player.server.sendMessage(message.append(" (Canceled)"));
        }
    }
}
