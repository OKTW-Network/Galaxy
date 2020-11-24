/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import one.oktw.galaxy.mixin.interfaces.IThrownCountdown_Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(ThrownEntity.class)
public class MixinThrownCountdown_ThrownEntity {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;getCollision(Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult addWaterHit(Entity entity, Predicate<Entity> predicate) {
        var hit = ProjectileUtil.getCollision(entity, predicate);

        if (hit.getType() == HitResult.Type.BLOCK && ((IThrownCountdown_Entity) this).getIntoWater() > 10) {
            var pos = entity.getPos();
            var velocity = pos.add(entity.getVelocity());
            var newHit = entity.world.raycast(new RaycastContext(pos, velocity, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, entity));
            if (newHit.getType() != HitResult.Type.MISS) {
                hit = newHit;
            }
        }

        return hit;
    }
}
