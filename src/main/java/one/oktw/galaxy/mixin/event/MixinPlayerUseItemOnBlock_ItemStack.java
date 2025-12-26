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

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import one.oktw.galaxy.event.EventManager;
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinPlayerUseItemOnBlock_ItemStack {
    @Inject(method = "useOn", at = @At(value = "HEAD"), cancellable = true)
    private void useItemOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getPlayer() == null) return;

        PlayerUseItemOnBlock event = EventManager.safeEmit(new PlayerUseItemOnBlock(context));
        if (event.getCancel()) {
            cir.setReturnValue(event.getSwing() ? InteractionResult.SUCCESS_SERVER : InteractionResult.CONSUME);
            cir.cancel();
        }
    }
}
