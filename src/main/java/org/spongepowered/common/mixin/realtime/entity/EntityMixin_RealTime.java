/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2024
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

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(Entity.class)
public abstract class EntityMixin_RealTime {
    @Shadow
    public int timeUntilRegen;
    @Shadow
    protected int ridingCooldown;

    @Shadow
    public abstract World getWorld();

    @Redirect(method = "baseTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;ridingCooldown:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Entity;stopRiding()V"
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/entity/Entity;horizontalSpeed:F",
                opcode = Opcodes.GETFIELD
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeEntityCooldown(final Entity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) this.getWorld()).realTimeBridge$getRealTimeTicks();
        this.ridingCooldown = Math.max(0, this.ridingCooldown - ticks);
    }
}
