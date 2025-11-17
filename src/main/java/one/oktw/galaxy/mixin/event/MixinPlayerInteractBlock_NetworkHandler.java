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

package one.oktw.galaxy.mixin.event;

import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityEvent;
import one.oktw.galaxy.event.EventManager;
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinPlayerInteractBlock_NetworkHandler extends ServerCommonPacketListenerImpl {
    @Shadow
    public ServerPlayer player;

    public MixinPlayerInteractBlock_NetworkHandler(MinecraftServer server, Connection connection, CommonListenerCookie clientData) {
        super(server, connection, clientData);
    }

    @Inject(method = "handleUseItemOn", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/level/ServerPlayerGameMode;useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
    ), cancellable = true)
    private void onPlayerInteractBlock(ServerboundUseItemOnPacket packet, CallbackInfo info) {
        PlayerInteractBlockEvent event = EventManager.safeEmit(new PlayerInteractBlockEvent(packet, player));
        if (event.getCancel()) {
            info.cancel();
            if (event.getSwing()) player.swing(packet.getHand(), true);
            // Re-sync block & inventory
            ServerLevel world = player.level();
            BlockPos blockPos = packet.getHitResult().getBlockPos();
            send(new ClientboundEntityEventPacket(player, EntityEvent.USE_ITEM_COMPLETE));
            send(new ClientboundSetHealthPacket(player.getHealth(), player.getFoodData().getFoodLevel(), player.getFoodData().getSaturationLevel()));
            send(new ClientboundBlockUpdatePacket(world, blockPos));
            send(new ClientboundBlockUpdatePacket(world, blockPos.relative(packet.getHitResult().getDirection())));
            player.containerMenu.sendAllDataToRemote();
        }
    }
}
