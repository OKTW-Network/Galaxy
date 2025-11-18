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

import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinAsyncChunk_ServerGamePacketListener {
    @Shadow
    public ServerPlayer player;

    @Shadow
    private static double clampHorizontal(double d) {
        return 0;
    }

    @Shadow
    public abstract void teleport(double x, double y, double z, float yaw, float pitch);

    @Inject(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"), cancellable = true)
    private void noBlockingMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (!packet.hasPosition()) return;

        int x = SectionPos.posToSectionCoord(clampHorizontal(packet.getX(this.player.getX())));
        int z = SectionPos.posToSectionCoord(clampHorizontal(packet.getZ(this.player.getZ())));
        if (!player.level().getChunkSource().isPositionTicking(ChunkPos.asLong(x, z))) {
            player.setDeltaMovement(Vec3.ZERO);
            teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot());
            ci.cancel();
        }
    }

    @Inject(method = "teleport(Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;teleportSetPosition(Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", shift = At.Shift.AFTER))
    private void onTeleport(PositionMoveRotation pos, Set<Relative> flags, CallbackInfo ci) {
        ServerLevel world = player.level();
        if (!world.players().contains(player)) return;
        world.getChunkSource().move(this.player);
    }
}
