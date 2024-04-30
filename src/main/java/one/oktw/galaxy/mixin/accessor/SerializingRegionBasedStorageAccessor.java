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

package one.oktw.galaxy.mixin.accessor;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.poi.PointOfInterestSet;
import net.minecraft.world.storage.SerializingRegionBasedStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(SerializingRegionBasedStorage.class)
public interface SerializingRegionBasedStorageAccessor {
    @Invoker
    @Nullable
    Optional<PointOfInterestSet> callGetIfLoaded(long pos);

    @Invoker
    CompletableFuture<Optional<NbtCompound>> callLoadNbt(ChunkPos pos);

    @Invoker
    void callUpdate(ChunkPos pos, RegistryOps<NbtElement> ops, @Nullable NbtCompound nbt);
}
