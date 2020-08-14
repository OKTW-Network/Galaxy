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

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import one.oktw.galaxy.worldData.ChunkDataProviderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class MixinChunkData_ServerWorld {
    @Inject(
        method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V",
        at = @At("RETURN")
    )
    public void onTickChunks(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci){
        //noinspection ConstantConditions
        ChunkDataProviderRegistry.Companion.getInstance().tickChunk((ServerWorld)((Object)this), chunk, randomTickSpeed);
    }
}
