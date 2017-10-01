package one.oktw.sponge.event;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;

public class DisablePortal {
    @Listener
    public void onMoveEntityEvent(MoveEntityEvent.Teleport.Portal portal, @Getter("getTargetEntity") Player player) {
        if (!player.hasPermission("oktw.world.portal")) portal.setCancelled(true);
    }
}
