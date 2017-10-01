package one.oktw.sponge.event;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

public class ForceGamemode {
    @Listener
    public void onMoveEntityEventEventTeleport(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player player) {
        World from = event.getFromTransform().getExtent();
        World to = event.getToTransform().getExtent();

        WorldProperties properties = to.getProperties();

        if (!from.equals(to)) {
            if (!player.hasPermission("oktw.world.gamemode")) {
                if (!properties.getGameMode().equals(player.gameMode().get())) {
                    player.offer(Keys.GAME_MODE, properties.getGameMode());
                }
            }
        }
    }
}
