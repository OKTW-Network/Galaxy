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

import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.poi.PointOfInterestSet;
import net.minecraft.world.poi.PointOfInterestStorage;
import one.oktw.galaxy.mixin.accessor.SerializingRegionBasedStorageAccessor;
import one.oktw.galaxy.mixin.accessor.StorageIoWorkerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ChunkSerializer.class)
public class MixinAsyncChunk_ChunkSerializer {
    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;initForPalette(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/ChunkSection;)V"))
    private static void AsyncPOIRead(PointOfInterestStorage pointOfInterestStorage, ChunkPos chunkPos, ChunkSection chunkSection) {
        Optional<PointOfInterestSet> optional = ((SerializingRegionBasedStorageAccessor) pointOfInterestStorage).callGetIfLoaded(ChunkSectionPos.from(chunkPos, 0).asLong());
        //noinspection OptionalAssignedToNull
        if (optional == null || optional.isEmpty()) {
            ((StorageIoWorkerAccessor) ((SerializingRegionBasedStorageAccessor) pointOfInterestStorage).getWorker()).callReadChunkData(chunkPos).thenAcceptAsync(compoundTag -> {
                ((SerializingRegionBasedStorageAccessor) pointOfInterestStorage).callUpdate(chunkPos, NbtOps.INSTANCE, compoundTag);
                pointOfInterestStorage.initForPalette(chunkPos, chunkSection);
            }, ((ServerWorld) ((SerializingRegionBasedStorageAccessor) pointOfInterestStorage).getWorld()).getServer());
        } else pointOfInterestStorage.initForPalette(chunkPos, chunkSection);
    }
}
