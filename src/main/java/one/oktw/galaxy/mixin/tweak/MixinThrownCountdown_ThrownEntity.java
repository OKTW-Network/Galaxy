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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import one.oktw.galaxy.mixin.interfaces.IThrownCountdown_Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(ThrowableProjectile.class)
public class MixinThrownCountdown_ThrownEntity {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getHitResultOnMoveVector(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Predicate;)Lnet/minecraft/world/phys/HitResult;"))
    private HitResult addWaterHit(Entity entity, Predicate<Entity> predicate) {
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(entity, predicate);

        if (hit.getType() == HitResult.Type.MISS && ((IThrownCountdown_Entity) this).galaxy$getIntoWater() > 10) {
            Vec3 pos = entity.position();
            Vec3 velocity = pos.add(entity.getDeltaMovement());
            BlockHitResult newHit = entity.level().clip(new ClipContext(pos, velocity, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, entity));
            if (newHit.getType() != HitResult.Type.MISS) {
                hit = newHit;
            }
        }

        return hit;
    }
}
