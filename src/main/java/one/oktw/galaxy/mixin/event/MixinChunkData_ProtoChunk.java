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

package one.oktw.galaxy.mixin.event;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import one.oktw.galaxy.worldData.ChunkDataProvider;
import one.oktw.galaxy.worldData.ChunkDataProviderRegistry;
import one.oktw.galaxy.worldData.ExtendedChunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ProtoChunk.class)
public class MixinChunkData_ProtoChunk implements ExtendedChunk {
    Map<String, Object> galaxyDataMap = new HashMap<>();

    @Inject(
        method = "<init>(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/UpgradeData;)V",
        at = @At("RETURN")
    )
    public void init(ChunkPos chunkPos, UpgradeData upgradeData, CallbackInfo ci){
        // The ReadOnlyChunk delegates WorldChunk and bypass the ProtoChunk completely, so we stop here
        //noinspection ConstantConditions
        if ((Object)this instanceof ReadOnlyChunk) {
            return;
        }
        if (upgradeData == UpgradeData.NO_UPGRADE_DATA) {
            for (ChunkDataProvider<Object> provider: ChunkDataProviderRegistry.Companion.getInstance().getProviders()) {
                setData(provider, ChunkDataProviderRegistry.Companion.getInstance().createData(chunkPos, provider));
            }
        }
    }

    public <T> T getData(@NotNull ChunkDataProvider<T> provider) {
        String name = ChunkDataProviderRegistry.Companion.getInstance().getRegisteredName((ChunkDataProvider<Object>) provider);
        //noinspection unchecked
        return (T) galaxyDataMap.get(name);
    }
    public <T> void setData(@NotNull ChunkDataProvider<T> provider, T data) {
        String name = ChunkDataProviderRegistry.Companion.getInstance().getRegisteredName((ChunkDataProvider<Object>) provider);
        galaxyDataMap.put(name, data);
    }
}
