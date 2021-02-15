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

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinFixBeacon_BeaconBlockEntity extends BlockEntity {
    @Shadow
    private int level;

    public MixinFixBeacon_BeaconBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    @Shadow
    public abstract void playSound(SoundEvent soundEvent);

    @Inject(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/sound/SoundEvents;BLOCK_BEACON_AMBIENT:Lnet/minecraft/sound/SoundEvent;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void fixBeaconActivate(CallbackInfo ci, int i, int j, int k, BlockPos blockPos2, BeaconBlockEntity.BeamSegment beamSegment, int l, int n) {
        boolean bl = n > 0;
        boolean bl2 = level > 0;
        if (!bl && bl2) {
            playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);

            //noinspection ConstantConditions
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, (new Box(i, j, k, i, j - 4, k)).expand(10.0D, 5.0D, 10.0D))) {
                Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, (BeaconBlockEntity) (Object) this);
            }
        } else if (bl && !bl2) {
            this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/World;isClient:Z"), cancellable = true)
    private void skipBugActivate(CallbackInfo ci) {
        ci.cancel();
    }
}
