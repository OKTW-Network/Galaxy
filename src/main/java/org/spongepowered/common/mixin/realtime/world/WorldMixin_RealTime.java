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
package org.spongepowered.common.mixin.realtime.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(World.class)
public abstract class WorldMixin_RealTime implements RealTimeTrackingBridge {
    @Shadow
    @Final
    protected LevelProperties properties;

    @Shadow
    public abstract void setTime(long long_1);

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Inject(method = "tickTime", at = @At("HEAD"))
    private void realTimeImpl$fixTimeOfDayForRealTime(CallbackInfo ci) {
        if (this.properties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            // Subtract the one the original tick method is going to add
            long diff = this.realTimeBridge$getRealTimeTicks() - 1;
            // Don't set if we're not changing it as other mods might be listening for changes
            if (diff > 0) {
                this.setTime(this.properties.getTimeOfDay() + diff);
            }
        }
    }

    @Override
    public long realTimeBridge$getRealTimeTicks() {
        if (this.getServer() != null) {
            return ((RealTimeTrackingBridge) this.getServer()).realTimeBridge$getRealTimeTicks();
        }
        return 1;
    }
}
