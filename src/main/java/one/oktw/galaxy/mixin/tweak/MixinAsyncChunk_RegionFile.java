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

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.world.storage.ChunkCompressionFormat;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.StorageKey;
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
    private FileChannel channel;

    @Shadow
    @Final
    StorageKey storageKey;

    @Shadow
    @Final
    ChunkCompressionFormat compressionFormat;

    @Shadow
    protected abstract int getSectorData(ChunkPos pos);

    @Shadow
    private static int getOffset(int sectorData) {
        return 0;
    }

    @Shadow
    private static int getSize(int sectorData) {
        return 0;
    }

    @Shadow
    private static boolean hasChunkStreamVersionId(byte b) {
        return false;
    }

    @Shadow
    private static byte getChunkStreamVersionId(byte b) {
        return 0;
    }

    @Shadow
    private static ByteArrayInputStream getInputStream(ByteBuffer buffer, int length) {
        return null;
    }

    @Shadow
    @Nullable
    protected abstract DataInputStream getInputStream(ChunkPos chunkPos, byte b) throws IOException;

    @Shadow
    @Nullable
    protected abstract DataInputStream decompress(ChunkPos chunkPos, byte b, InputStream inputStream) throws IOException;

    @Inject(method = "delete", at = @At("HEAD"))
    private void deleteLock(ChunkPos chunkPos, CallbackInfo ci) {
        lock.lock();
    }

    @Inject(method = "delete", at = @At("RETURN"))
    private void deleteUnlock(ChunkPos chunkPos, CallbackInfo ci) {
        lock.unlock();
    }

    @Inject(method = "writeChunk", at = @At("HEAD"))
    private void writeChunkLock(ChunkPos pos, ByteBuffer byteBuffer, CallbackInfo ci) {
        lock.lock();
    }

    @Inject(method = "writeChunk", at = @At("RETURN"))
    private void writeChunkUnlock(ChunkPos pos, ByteBuffer byteBuffer, CallbackInfo ci) {
        lock.unlock();
    }

    @Inject(method = "getSectorData", at = @At("HEAD"))
    private void getSectorDataLock(ChunkPos pos, CallbackInfoReturnable<Integer> cir) {
        lock.lock();
    }

    @Inject(method = "getSectorData", at = @At("RETURN"))
    private void getSectorDataUnlock(ChunkPos pos, CallbackInfoReturnable<Integer> cir) {
        lock.unlock();
    }

    // Remove synchronized.
    @Override
    public DataInputStream galaxy$getChunkInputStreamNoSync(ChunkPos pos) throws IOException {
        int i = getSectorData(pos);
        if (i == 0) return null;
        int start = getOffset(i);
        int count = getSize(i);
        int length = count * 4096;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        channel.read(byteBuffer, start * 4096L);
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
        if (hasChunkStreamVersionId(b)) {
            if (n != 0) LOGGER.warn("Chunk has both internal and external streams");
            return getInputStream(pos, getChunkStreamVersionId(b));
        }
        if (n > byteBuffer.remaining()) {
            LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", pos, n, byteBuffer.remaining());
            return null;
        }
        if (n < 0) {
            LOGGER.error("Declared size {} of chunk {} is negative", m, pos);
            return null;
        }
        FlightProfiler.INSTANCE.onChunkRegionRead(storageKey, pos, compressionFormat, n);
        return decompress(pos, b, getInputStream(byteBuffer, n));
    }
}
