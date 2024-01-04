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

import net.minecraft.entity.EntityStatuses;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import one.oktw.galaxy.event.EventManager;
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinPlayerInteractBlock_NetworkHandler extends ServerCommonNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    public MixinPlayerInteractBlock_NetworkHandler(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Inject(method = "onPlayerInteractBlock", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"
    ), cancellable = true)
    private void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo info) {
        PlayerInteractBlockEvent event = EventManager.safeEmit(new PlayerInteractBlockEvent(packet, player));
        if (event.getCancel()) {
            info.cancel();
            if (event.getSwing()) player.swingHand(packet.getHand(), true);
            // Re-sync block & inventory
            ServerWorld world = player.getServerWorld();
            BlockPos blockPos = packet.getBlockHitResult().getBlockPos();
            sendPacket(new EntityStatusS2CPacket(player, EntityStatuses.CONSUME_ITEM));
            sendPacket(new HealthUpdateS2CPacket(player.getHealth(), player.getHungerManager().getFoodLevel(), player.getHungerManager().getSaturationLevel()));
            sendPacket(new BlockUpdateS2CPacket(world, blockPos));
            sendPacket(new BlockUpdateS2CPacket(world, blockPos.offset(packet.getBlockHitResult().getSide())));
            player.currentScreenHandler.syncState();
        }
    }
}
