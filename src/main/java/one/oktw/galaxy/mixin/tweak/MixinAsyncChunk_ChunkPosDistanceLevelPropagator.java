/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkPosDistanceLevelPropagator.class)
public abstract class MixinAsyncChunk_ChunkPosDistanceLevelPropagator {
    @Redirect(method = "propagateLevel", at = @At(value = "NEW", target = "net/minecraft/util/math/ChunkPos"))
    private ChunkPos skipCreateChunkPos(long pos) {
        return ChunkPos.ORIGIN;
    }

    @Redirect(method = "propagateLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/ChunkPos;x:I"))
    private int getX(ChunkPos instance, long pos, int level, boolean decrease) {
        return (int) pos;
    }

    @Redirect(method = "propagateLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/ChunkPos;z:I"))
    private int getZ(ChunkPos instance, long pos, int level, boolean decrease) {
        return (int) (pos >> 32);
    }

    @Redirect(method = "recalculateLevel", at = @At(value = "NEW", target = "net/minecraft/util/math/ChunkPos"))
    private ChunkPos skipCreateChunkPos2(long pos) {
        return ChunkPos.ORIGIN;
    }

    @Redirect(method = "recalculateLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/ChunkPos;x:I"))
    private int getX2(ChunkPos instance, long pos, long excludedId, int maxLevel) {
        return (int) pos;
    }

    @Redirect(method = "recalculateLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/ChunkPos;z:I"))
    private int getZ2(ChunkPos instance, long pos, long excludedId, int maxLevel) {
        return (int) (pos >> 32);
    }
}
