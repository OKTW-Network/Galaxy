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

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import one.oktw.galaxy.mixin.accessor.BeaconLevelAccessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinFixBeacon_BeaconBlockEntity extends BlockEntity {
    public MixinFixBeacon_BeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public static void playSound(World world, BlockPos pos, SoundEvent sound) {
    }

    @Inject(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/sound/SoundEvents;BLOCK_BEACON_AMBIENT:Lnet/minecraft/sound/SoundEvent;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    // TODO check local capture
    private static void fixBeaconActivate(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci, int i, int j, int k, BlockPos blockPos2, BeaconBlockEntity.BeamSegment beamSegment, int l, int n) {
        BeaconLevelAccessor accessor = (BeaconLevelAccessor) blockEntity;
        boolean bl = n > 0;
        boolean bl2 = accessor.getLevel() > 0;
        if (!bl && bl2) {
            playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);

            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, (new Box(i, j, k, i, j - 4, k)).expand(10.0D, 5.0D, 10.0D))) {
                Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, accessor.getLevel());
            }
        } else if (bl && !bl2) {
            playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z"), cancellable = true)
    private static void skipBugActivate(CallbackInfo ci) {
        ci.cancel();
    }
}
