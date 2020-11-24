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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import one.oktw.galaxy.mixin.interfaces.IThrownCountdown_Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinThrownCountdown_Entity implements IThrownCountdown_Entity {
    private int intoWater = 0;

    @Shadow
    public abstract void remove();

    @Inject(method = "checkWaterState",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onSwimmingStart()V"))
    private void countIntoWater(CallbackInfo ci) {
        //noinspection ConstantConditions
        if (((Object) this) instanceof ArrowEntity || ((Object) this) instanceof SpectralArrowEntity) {
            if (intoWater > 10) {
                remove();
            }
        }
        intoWater++;
    }

    @Override
    public int getIntoWater() {
        return intoWater;
    }
}
