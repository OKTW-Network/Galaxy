package one.oktw.galaxy.event

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter

class DisablePortal {
    @Listener
    fun onMoveEntityEvent(portal: MoveEntityEvent.Teleport.Portal, @Getter("getTargetEntity") player: Player) {
        // TODO
    }
}
