package one.oktw.sponge.event;

import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class DisablePortal {
    private Server server = Sponge.getServer();

    @Listener
    public void onMoveEntityEvent(MoveEntityEvent.Teleport.Portal portal, @Getter("getTargetEntity") Player player) {
        if (!portal.getFromTransform().getExtent().getProperties().getWorldName().equals(server.getDefaultWorldName())) {
            player.sendMessages(Text.of(TextColors.RED, "此路不通！ "));
            portal.setToTransform(portal.getFromTransform());
        }
    }
}
