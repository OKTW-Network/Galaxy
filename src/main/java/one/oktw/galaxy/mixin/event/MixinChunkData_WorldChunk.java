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

import net.minecraft.world.World;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
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

@Mixin(WorldChunk.class)
public class MixinChunkData_WorldChunk implements ExtendedChunk {
    Map<String, Object> galaxyDataMap = new HashMap<>();

    @Inject(
        method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ProtoChunk;)V",
        at = @At("RETURN")
    )
    public void init(World world, ProtoChunk protoChunk, CallbackInfo ci){
        // When the WorldChunk upgraded from the protoChunk,
        // the custom data on the protoChunk need to be moved to the WorldChunk
        for (ChunkDataProvider<Object> provider: ChunkDataProviderRegistry.Companion.getInstance().getProviders()) {
            setData(provider, ((ExtendedChunk)protoChunk).getData(provider));
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
