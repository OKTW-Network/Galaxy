/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import one.oktw.galaxy.Main;
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
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;isEditable()Z"),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onSignUpdate(UpdateSignC2SPacket updateSignC2SPacket, List<String> list, CallbackInfo ci, ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity, SignBlockEntity signBlockEntity) {
        Main main = Main.Companion.getMain();
        if (main == null) return;
        if (main.getEventManager().emit(new PlayerUpdateSignEvent(updateSignC2SPacket, player, signBlockEntity)).getCancel()) {
            ci.cancel();

            signBlockEntity.markDirty();
            serverWorld.updateListeners(blockPos, blockState, blockState, 3);
        }
    }
}
