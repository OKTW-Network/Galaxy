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

package one.oktw.galaxy.mixin.event;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import one.oktw.galaxy.event.EventManager;
import one.oktw.galaxy.event.type.PlayerUpdateSignEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinPlayerUpdateSign_NetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;Ljava/util/List;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;tryChangeText(Lnet/minecraft/entity/player/PlayerEntity;ZLjava/util/List;)V"),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onSignUpdate(UpdateSignC2SPacket packet, List<FilteredMessage> signText, CallbackInfo ci, ServerWorld serverWorld, BlockPos blockPos, BlockEntity blockEntity, SignBlockEntity signBlockEntity) {
        if (EventManager.safeEmit(new PlayerUpdateSignEvent(packet, player, signBlockEntity)).getCancel()) {
            ci.cancel();

            signBlockEntity.markDirty();
            serverWorld.updateListeners(blockPos, signBlockEntity.getCachedState(), signBlockEntity.getCachedState(), Block.NOTIFY_ALL);
        }
    }
}
