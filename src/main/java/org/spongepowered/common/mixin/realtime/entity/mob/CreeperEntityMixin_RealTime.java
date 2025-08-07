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
package org.spongepowered.common.mixin.realtime.entity.mob;

import net.minecraft.entity.mob.CreeperEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin_RealTime {
    private boolean delay;

    @Shadow
    private int currentFuseTime;
    @Shadow
    private int fuseTime;

    @Shadow
    public abstract int getFuseSpeed();

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/mob/CreeperEntity;currentFuseTime:I",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/CreeperEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"),
            to = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0)
        )
    )
    private void realTimeImpl$adjustForRealTimeCreeperFuseTime(final CreeperEntity self, final int modifier) {
        if (modifier != 0) {
            final int ticks = (int) ((RealTimeTrackingBridge) self.getWorld()).realTimeBridge$getRealTimeTicks();
            this.currentFuseTime += (getFuseSpeed() * ticks);

            // delay 1 tick wait AI detect player distance
            if (currentFuseTime >= fuseTime && !delay) {
                delay = true;
                currentFuseTime = fuseTime - 1;
            } else if (delay) {
                delay = false;
            }
        }
    }
}
