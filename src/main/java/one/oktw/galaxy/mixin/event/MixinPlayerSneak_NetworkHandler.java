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

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerSneakEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinPlayerSneak_NetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    @Redirect(method = "onClientCommand", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSneaking(Z)V"
    ))
    private void playerInput(ServerPlayerEntity serverPlayerEntity, boolean sneaking) {
        if (sneaking) {
            Main main = Main.Companion.getMain();
            if (main == null) return;
            main.getEventManager().emit(new PlayerSneakEvent(player));

        }

        player.setSneaking(sneaking);
    }
}
