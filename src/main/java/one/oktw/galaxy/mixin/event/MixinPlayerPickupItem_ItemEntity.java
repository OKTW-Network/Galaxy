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

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerPickupItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class MixinPlayerPickupItem_ItemEntity {
    @Inject(
        method = "onPlayerCollision",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;triggerItemPickedUpByEntityCriteria(Lnet/minecraft/entity/ItemEntity;)V",
            shift = At.Shift.AFTER
        ))
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        Main main = Main.Companion.getMain();
        if (main == null) return;
        main.getEventManager().emit(new PlayerPickupItemEvent((ServerPlayerEntity) player));
    }
}
