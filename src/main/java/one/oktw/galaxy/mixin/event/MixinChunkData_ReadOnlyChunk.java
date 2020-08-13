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

import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import one.oktw.galaxy.worldData.ChunkDataProvider;
import one.oktw.galaxy.worldData.ChunkDataProviderRegistry;
import one.oktw.galaxy.worldData.ExtendedChunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;
import java.util.Map;

@Mixin(ReadOnlyChunk.class)
public class MixinChunkData_ReadOnlyChunk implements ExtendedChunk {
    public <T> T getData(@NotNull ChunkDataProvider<T> provider) {
        @SuppressWarnings("ConstantConditions") WorldChunk original = ((ReadOnlyChunk) ((Object) this)).getWrappedChunk();
        ExtendedChunk casted = ((ExtendedChunk) original);
        return casted.getData(provider);
    }
    public <T> void setData(@NotNull ChunkDataProvider<T> provider, T data) {
        @SuppressWarnings("ConstantConditions") WorldChunk original = ((ReadOnlyChunk) ((Object) this)).getWrappedChunk();
        ExtendedChunk casted = ((ExtendedChunk) original);
        casted.setData(provider, data);
    }
}
