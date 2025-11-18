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
package org.spongepowered.common.mixin.realtime.blockentity;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;
import org.spongepowered.common.mixin.realtime.accessor.AbstractFurnaceBlockEntityAccessor;

@Mixin(value = AbstractFurnaceBlockEntity.class, priority = 1001)
public abstract class AbstractFurnaceBlockEntityMixin_RealTime {
    @Redirect(method = "serverTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;litTimeRemaining:I",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0
        )
    )
    private static void realTimeImpl$adjustForRealTimeBurnTime(AbstractFurnaceBlockEntity self, int value) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getLevel()).realTimeBridge$getRealTimeTicks();

        AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) self;
        final int burnTime = accessor.getLitTimeRemaining();
        if (burnTime != 1 && burnTime < ticks) {
            accessor.setLitTimeRemaining(1); // last cook tick
        } else {
            accessor.setLitTimeRemaining(Math.max(0, burnTime - ticks));
        }
    }

    @Redirect(
        method = "serverTick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;cookingTimer:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private static void realTimeImpl$adjustForRealTimeCookTime(final AbstractFurnaceBlockEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getLevel()).realTimeBridge$getRealTimeTicks();

        AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) self;
        accessor.setCookingTimer(Math.min(accessor.getCookingTotalTime(), accessor.getCookingTimer() + ticks));
    }

    @Redirect(
        method = "serverTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;cookingTimer:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/Mth;clamp(III)I"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
        )
    )
    private static void realTimeImpl$adjustForRealTimeCookTimeCooldown(final AbstractFurnaceBlockEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) self.getLevel()).realTimeBridge$getRealTimeTicks();

        AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) self;
        accessor.setCookingTimer(Mth.clamp(accessor.getCookingTimer() - (2 * ticks), 0, accessor.getCookingTotalTime()));
    }
}
