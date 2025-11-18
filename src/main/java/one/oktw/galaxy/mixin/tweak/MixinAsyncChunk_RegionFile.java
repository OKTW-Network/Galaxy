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

import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import one.oktw.galaxy.mixin.interfaces.RegionFileInputStream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(RegionFile.class)
public abstract class MixinAsyncChunk_RegionFile implements RegionFileInputStream {
    @Unique
    private final ReentrantLock lock = new ReentrantLock();

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private FileChannel file;

    @Shadow
    @Final
    RegionStorageInfo info;

    @Shadow
    @Final
    RegionFileVersion version;

    @Shadow
    protected abstract int getOffset(ChunkPos pos);

    @Shadow
    private static int getSectorNumber(int sectorData) {
        return 0;
    }

    @Shadow
    private static int getNumSectors(int sectorData) {
        return 0;
    }

    @Shadow
    private static boolean isExternalStreamChunk(byte b) {
        return false;
    }

    @Shadow
    private static byte getExternalChunkVersion(byte b) {
        return 0;
    }

    @Shadow
    private static ByteArrayInputStream createStream(ByteBuffer buffer, int length) {
        return null;
    }

    @Shadow
    @Nullable
    protected abstract DataInputStream createExternalChunkInputStream(ChunkPos chunkPos, byte b) throws IOException;

    @Shadow
    @Nullable
    protected abstract DataInputStream createChunkInputStream(ChunkPos chunkPos, byte b, InputStream inputStream) throws IOException;

    @Inject(method = "clear", at = @At("HEAD"))
    private void deleteLock(ChunkPos chunkPos, CallbackInfo ci) {
        lock.lock();
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void deleteUnlock(ChunkPos chunkPos, CallbackInfo ci) {
        lock.unlock();
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void writeChunkLock(ChunkPos pos, ByteBuffer byteBuffer, CallbackInfo ci) {
        lock.lock();
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeChunkUnlock(ChunkPos pos, ByteBuffer byteBuffer, CallbackInfo ci) {
        lock.unlock();
    }

    @Inject(method = "getOffset", at = @At("HEAD"))
    private void getSectorDataLock(ChunkPos pos, CallbackInfoReturnable<Integer> cir) {
        lock.lock();
    }

    @Inject(method = "getOffset", at = @At("RETURN"))
    private void getSectorDataUnlock(ChunkPos pos, CallbackInfoReturnable<Integer> cir) {
        lock.unlock();
    }

    // Remove synchronized.
    @Override
    public DataInputStream galaxy$getChunkInputStreamNoSync(ChunkPos pos) throws IOException {
        int i = getOffset(pos);
        if (i == 0) return null;
        int start = getSectorNumber(i);
        int count = getNumSectors(i);
        int length = count * 4096;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        file.read(byteBuffer, start * 4096L);
        byteBuffer.flip();
        if (byteBuffer.remaining() < 5) {
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", pos, length, byteBuffer.remaining());
            return null;
        }
        int m = byteBuffer.getInt();
        byte b = byteBuffer.get();
        if (m == 0) {
            LOGGER.warn("Chunk {} is allocated, but stream is missing", pos);
            return null;
        }
        int n = m - 1;
        if (isExternalStreamChunk(b)) {
            if (n != 0) LOGGER.warn("Chunk has both internal and external streams");
            return createExternalChunkInputStream(pos, getExternalChunkVersion(b));
        }
        if (n > byteBuffer.remaining()) {
            LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", pos, n, byteBuffer.remaining());
            return null;
        }
        if (n < 0) {
            LOGGER.error("Declared size {} of chunk {} is negative", m, pos);
            return null;
        }
        JvmProfiler.INSTANCE.onRegionFileRead(info, pos, version, n);
        return createChunkInputStream(pos, b, createStream(byteBuffer, n));
    }
}
