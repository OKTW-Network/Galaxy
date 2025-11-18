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

package org.spongepowered.common.mixin.realtime.world.dimension;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(PortalProcessor.class)
public abstract class PortalProcessorMixin_RealTime {
    @Shadow
    private int portalTime;

    @Inject(method = "processPortalTeleportation", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/PortalProcessor;portalTime:I", opcode = Opcodes.GETFIELD))
    private void realTimeImpl$adjustForRealTimePortalCounter(ServerLevel world, Entity entity, boolean canUsePortals, CallbackInfoReturnable<Boolean> cir) {
        final int ticks = (int) ((RealTimeTrackingBridge) world).realTimeBridge$getRealTimeTicks() - 1;
        this.portalTime += Math.max(0, ticks);
    }

    @Inject(method = "processPortalTeleportation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PortalProcessor;decayTick()V"))
    private void realTimeImpl$PortalDecayCounter(ServerLevel world, Entity entity, boolean canUsePortals, CallbackInfoReturnable<Boolean> cir) {
        final int ticks = (int) ((RealTimeTrackingBridge) world).realTimeBridge$getRealTimeTicks() - 1;
        if (ticks > 0) {
            this.portalTime = Math.max(0, this.portalTime - ticks * 4);
        }
    }
}
