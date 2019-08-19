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

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(value = AbstractFurnaceBlockEntity.class, priority = 1001)
public abstract class AbstractFurnaceBlockEntityMixin_RealTime extends BlockEntity {
    @Shadow
    private int burnTime;
    @Shadow
    private int cookTime;
    @Shadow
    private int cookTimeTotal;

    public AbstractFurnaceBlockEntityMixin_RealTime(BlockEntityType<?> blockEntityType_1) {
        super(blockEntityType_1);
    }

    @Redirect(method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;burnTime:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;isBurning()Z",
                opcode = 1
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;world:Lnet/minecraft/world/World;",
                opcode = Opcodes.GETFIELD,
                ordinal = 0
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeBurnTime(final AbstractFurnaceBlockEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) this.getWorld()).realTimeBridge$getRealTimeTicks();
        this.burnTime = Math.max(0, this.burnTime - Math.max(1, ticks - 1));
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;cookTime:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;canAcceptRecipeOutput(Lnet/minecraft/recipe/Recipe;)Z",
                ordinal = 1
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;cookTimeTotal:I",
                opcode = Opcodes.GETFIELD,
                ordinal = 0
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeCookTime(final AbstractFurnaceBlockEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) this.getWorld()).realTimeBridge$getRealTimeTicks();
        this.cookTime = Math.min(this.cookTimeTotal, this.cookTime + ticks);
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;cookTime:I",
            opcode = Opcodes.PUTFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;isBurning()Z"
            )
        )
    )
    private void realTimeImpl$adjustForRealTimeCookTimeCooldown(final AbstractFurnaceBlockEntity self, final int modifier) {
        final int ticks = (int) ((RealTimeTrackingBridge) this.getWorld()).realTimeBridge$getRealTimeTicks();
        this.cookTime = MathHelper.clamp(this.cookTime - (2 * ticks), 0, this.cookTimeTotal);
    }
}
