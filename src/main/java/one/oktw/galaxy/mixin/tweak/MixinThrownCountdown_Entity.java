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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import one.oktw.galaxy.mixin.interfaces.IThrownCountdown_Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinThrownCountdown_Entity implements IThrownCountdown_Entity {
    @Unique
    private int intoWater = 0;

    @Shadow
    public abstract void discard();

    @Inject(method = "updateInWaterStateAndDoWaterCurrentPushing",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;doWaterSplashEffect()V"))
    private void countIntoWater(CallbackInfo ci) {
        //noinspection ConstantConditions
        if (((Object) this) instanceof Arrow || ((Object) this) instanceof SpectralArrow) {
            if (intoWater > 10) {
                discard();
            }
        }
        intoWater++;
    }

    @Override
    public int galaxy$getIntoWater() {
        return intoWater;
    }
}
