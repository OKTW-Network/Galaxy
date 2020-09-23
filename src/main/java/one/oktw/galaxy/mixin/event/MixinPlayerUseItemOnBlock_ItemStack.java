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

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinPlayerUseItemOnBlock_ItemStack {
    @Inject(method = "useOnBlock", at = @At(value = "HEAD"))
    private void useItemOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        Main main = Main.Companion.getMain();
        if (main == null || context.getPlayer() == null) return;
        main.getEventManager().emit(new PlayerUseItemOnBlock(context));
    }
}
