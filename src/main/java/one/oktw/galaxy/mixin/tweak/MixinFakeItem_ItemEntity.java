/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import one.oktw.galaxy.mixin.interfaces.FakeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinFakeItem_ItemEntity extends Entity implements FakeEntity {
    private boolean fake = false;

    public MixinFakeItem_ItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void setFake(boolean value) {
        noClip = value;
        fake = value;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void fakeTick(CallbackInfo ci) {
        if (fake) {
            Vec3d velocity = getVelocity();
            Vec3d adjustedVelocity = adjustMovementForCollisions(velocity, getBoundingBox(), new ReusableStream<>(world.getBlockCollisions(this, getBoundingBox().stretch(velocity))));
            // Tick velocity
            move(MovementType.SELF, velocity);

            // Send velocity to client
            if (!adjustedVelocity.equals(velocity)) {
                velocityDirty = true;
                velocityModified = true;
            }
            if ((this.age + this.getId()) % 4 == 0) {
                velocityModified = true;
            }

            ci.cancel();
        }
    }
}
