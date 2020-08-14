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

import net.minecraft.world.chunk.WorldChunk;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.ChunkLoadEvent;
import one.oktw.galaxy.event.type.ChunkUnloadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class MixinChunkEvent_WorldChunk {
    @Inject(
        method = "setLoadedToWorld(Z)V",
        at = @At("HEAD")
    )
    public void setLoaded(boolean loaded, CallbackInfo ci) {
        //noinspection ConstantConditions
        WorldChunk original = ((WorldChunk) ((Object) this));
        if (loaded) {
            //noinspection ConstantConditions
            Main.Companion.getMain().getEventManager().emit(new ChunkLoadEvent(original.getWorld(), original));
        } else {
            //noinspection ConstantConditions
            Main.Companion.getMain().getEventManager().emit(new ChunkUnloadEvent(original.getWorld(), original));
        }
    }
}
