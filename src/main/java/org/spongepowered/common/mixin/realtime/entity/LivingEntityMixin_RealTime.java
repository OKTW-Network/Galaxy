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

/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.realtime.entity;

import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin_RealTime extends EntityMixin_RealTime {
    @Shadow
    public int deathTime;

    @Shadow
    protected int despawnCounter;

    @Shadow
    protected int itemUseTimeLeft;

    @Shadow
    protected int lastAttackedTicks;

    @Shadow
    public int hurtTime;

    @Redirect(method = "updatePostDeath",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;deathTime:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void realTimeImpl$adjustForRealTimeDeathTime(final LivingEntity self, final int vanillaNewDeathTime) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getEntityWorld()).realTimeBridge$getRealTimeTicks();
        int newDeathTime = this.deathTime + ticks;
        // At tick 20, XP is dropped and the death animation finishes. The
        // entity is also removed from the world... except in the case of
        // players, which are not removed until they log out or click Respawn.
        // For players, then, let the death time pass 20 to avoid XP
        // multiplication - not just duplication, but *multiplication*.
        if (vanillaNewDeathTime <= 20 && newDeathTime > 20) {
            newDeathTime = 20;
        }
        this.deathTime = newDeathTime;
    }

    // TODO rewrite this mixin
    @Redirect(method = "tickActiveItemStack", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;itemUseTimeLeft:I", opcode = Opcodes.GETFIELD))
    private int realTimeImpl$adjustForRealTimeUseTime(final LivingEntity self) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getEntityWorld()).realTimeBridge$getRealTimeTicks();
        return itemUseTimeLeft - Math.min(itemUseTimeLeft, ticks);
    }

    @Redirect(method = "tickActiveItemStack", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;itemUseTimeLeft:I", opcode = Opcodes.PUTFIELD))
    private void realTimeImpl$adjustForRealTimeUseTime(final LivingEntity self, final int modifier) {
        // Skip original
    }

    @Redirect(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I", opcode = Opcodes.PUTFIELD))
    private void realTimeImpl$adjustForRealTimeHurtTime(final LivingEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getEntityWorld()).realTimeBridge$getRealTimeTicks();
        hurtTime -= Math.min(hurtTime, ticks);
    }

    @Redirect(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;timeUntilRegen:I", opcode = Opcodes.PUTFIELD))
    private void realTimeImpl$adjustForRealTimeUntilRegen(final LivingEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getEntityWorld()).realTimeBridge$getRealTimeTicks();
        timeUntilRegen -= Math.min(timeUntilRegen, ticks);
    }
}
