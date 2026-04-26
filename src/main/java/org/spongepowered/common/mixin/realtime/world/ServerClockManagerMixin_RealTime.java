/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2026
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

package org.spongepowered.common.mixin.realtime.world;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

import java.util.Map;

@Mixin(ServerClockManager.class)
public abstract class ServerClockManagerMixin_RealTime implements RealTimeTrackingBridge {
    @Shadow
    private MinecraftServer server;

    @Final
    @Shadow
    private Map<Holder<WorldClock>, ServerClockManager.ClockInstance> clocks;

    @Inject(method = "tick", at=@At("HEAD"))
    private void realTimeImpl$fixTimeOfDayForRealTime(CallbackInfo ci){
        //noinspection deprecation
        if (this.server.getGlobalGameRules().get(GameRules.ADVANCE_TIME)) {
            // Subtract the one the original tick method is going to add
            long diff = this.realTimeBridge$getRealTimeTicks() - 1;
            // Don't set if we're not changing it as other mods might be listening for changes
            if (diff > 0) {
                for (int i = 0; i < diff; i++) {
                    this.clocks.values().forEach(ServerClockManager.ClockInstance::tick);
                }
            }
        }
    }

    @Override
    public long realTimeBridge$getRealTimeTicks() {
        if (this.server != null) {
            return ((RealTimeTrackingBridge) this.server).realTimeBridge$getRealTimeTicks();
        }
        return 1;
    }
}
