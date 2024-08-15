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

package org.spongepowered.common.mixin.realtime.world.dimension;

import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.PortalManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.bridge.RealTimeTrackingBridge;

@Mixin(PortalManager.class)
public abstract class PortalManagerMixin_RealTime {
    @Shadow
    private Portal portal;
    @Shadow
    private int ticksInPortal;
    @Shadow
    private boolean inPortal;

    @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
    private void realTimeImpl$adjustForRealTimePortalCounter(ServerWorld world, Entity entity, boolean canUsePortals, CallbackInfoReturnable<Boolean> cir) {
        if (this.inPortal) {
            final int ticks = (int) ((RealTimeTrackingBridge) world).realTimeBridge$getRealTimeTicks();
            this.ticksInPortal += ticks;
            // FIXME: Does this still need to this.ticksInPortal++ ?
            cir.setReturnValue(canUsePortals && this.ticksInPortal >= this.portal.getPortalDelay(world, entity));
        }
    }
}
