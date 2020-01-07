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

import net.minecraft.entity.ExperienceOrbEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin_RealTime extends EntityMixin_RealTime {
    @Shadow
    public int pickupDelay;
    @Shadow
    public int orbAge;

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/ExperienceOrbEntity;pickupDelay:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void realTimeImpl$adjustForRealTimePickupDelay(final ExperienceOrbEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) this.world).realTimeBridge$getRealTimeTicks();
        this.pickupDelay = Math.max(0, this.pickupDelay - ticks);
    }

    @Redirect(
        method = "tick",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/entity/ExperienceOrbEntity;orbAge:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/entity/ExperienceOrbEntity;renderTicks:I",
                opcode = Opcodes.PUTFIELD
            ),
            to = @At(
                value = "CONSTANT",
                args = "intValue=6000"
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeAge(final ExperienceOrbEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getEntityWorld()).realTimeBridge$getRealTimeTicks();
        this.orbAge += ticks;
    }
}
