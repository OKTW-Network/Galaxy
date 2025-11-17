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

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import one.oktw.galaxy.mixin.interfaces.RegionFileInputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(RegionFileStorage.class)
public abstract class MixinAsyncChunk_RegionBasedStorage {
    @Unique
    private final ReentrantLock lock = new ReentrantLock();

    @Inject(method = "getRegionFile", at = @At("HEAD"))
    private void readLock(ChunkPos pos, CallbackInfoReturnable<RegionFile> cir) {
        lock.lock();
    }

    @Inject(method = "getRegionFile", at = @At("RETURN"))
    private void readUnlock(ChunkPos pos, CallbackInfoReturnable<RegionFile> cir) {
        lock.unlock();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void closeLock(CallbackInfo ci) {
        lock.lock();
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void closeUnlock(CallbackInfo ci) {
        lock.unlock();
    }

    @Inject(method = "flush", at = @At("HEAD"))
    private void syncLock(CallbackInfo ci) {
        lock.lock();
    }

    @Inject(method = "flush", at = @At("RETURN"))
    private void syncUnlock(CallbackInfo ci) {
        lock.unlock();
    }

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/RegionFile;getChunkDataInputStream(Lnet/minecraft/world/level/ChunkPos;)Ljava/io/DataInputStream;"))
    private DataInputStream overwriteGetInputStream(RegionFile regionFile, ChunkPos pos) throws IOException {
        return ((RegionFileInputStream) regionFile).galaxy$getChunkInputStreamNoSync(pos);
    }

    @Redirect(method = "scanChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/RegionFile;getChunkDataInputStream(Lnet/minecraft/world/level/ChunkPos;)Ljava/io/DataInputStream;"))
    private DataInputStream overwriteGetInputStream2(RegionFile regionFile, ChunkPos pos) throws IOException {
        return ((RegionFileInputStream) regionFile).galaxy$getChunkInputStreamNoSync(pos);
    }
}
