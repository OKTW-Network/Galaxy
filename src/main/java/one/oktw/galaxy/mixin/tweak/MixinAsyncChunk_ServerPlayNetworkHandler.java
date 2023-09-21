/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinAsyncChunk_ServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    private static double clampHorizontal(double d) {
        return 0;
    }

    @Shadow
    public abstract void requestTeleport(double x, double y, double z, float yaw, float pitch);

    @Inject(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    private void noBlockingMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (!packet.changesPosition()) return;

        int x = ChunkSectionPos.getSectionCoord(clampHorizontal(packet.getX(this.player.getX())));
        int z = ChunkSectionPos.getSectionCoord(clampHorizontal(packet.getZ(this.player.getZ())));
        if (!player.getServerWorld().getChunkManager().isTickingFutureReady(ChunkPos.toLong(x, z))) {
            player.setVelocity(Vec3d.ZERO);
            requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
            ci.cancel();
        }
    }

    @Inject(method = "requestTeleport(DDDFFLjava/util/Set;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V", shift = At.Shift.AFTER))
    private void onTeleport(double x, double y, double z, float yaw, float pitch, Set<PositionFlag> set, CallbackInfo ci) {
        ServerWorld world = player.getServerWorld();
        if (!world.getPlayers().contains(player)) return;
        world.getChunkManager().updatePosition(this.player);
    }
}
