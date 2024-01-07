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

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.HotBarSlotUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"net/minecraft/server/network/ServerPlayerEntity$2"})
public class MixinHotBarSlotUpdate_ScreenHandlerListener {
    @Shadow(aliases = {"field_29183"})
    private ServerPlayerEntity player;

    @Inject(
        method = "onSlotUpdate(Lnet/minecraft/screen/ScreenHandler;ILnet/minecraft/item/ItemStack;)V",
        at = @At(value = "RETURN")
    )
    private void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack, CallbackInfo ci) {
        Main main = Main.Companion.getMain();
        if (main == null) return;
        if (slotId >= 36 && slotId <= 45) {
            main.getEventManager().emit(new HotBarSlotUpdateEvent(player, handler, slotId, stack));
        }
    }
}
