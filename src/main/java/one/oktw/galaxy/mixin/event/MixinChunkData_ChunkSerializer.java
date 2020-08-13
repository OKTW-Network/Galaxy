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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import one.oktw.galaxy.worldData.ChunkDataProviderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class MixinChunkData_ChunkSerializer {
    @Inject(
        method = "serialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/nbt/CompoundTag;",
        at = @At(
            value = "RETURN"
        )
    )
    private static void writeCustomData(ServerWorld world, Chunk chunk, CallbackInfoReturnable<CompoundTag> cir) {
        ChunkDataProviderRegistry.Companion.getInstance().writeData(world, chunk, cir.getReturnValue().getCompound("Level"));
    }

    @Inject(
        method = "deserialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/world/poi/PointOfInterestStorage;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/chunk/ProtoChunk;",
        at = @At(
            value = "RETURN"
        )
    )
    private static void parseCustomData(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
        ProtoChunk chunk = cir.getReturnValue();
        ChunkDataProviderRegistry.Companion.getInstance().parseData(world, pos, tag.getCompound("Level"), chunk);
    }
}
