package one.oktw.galaxy.event

import one.oktw.galaxy.internal.TravelerManager
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

class TravelerWatcher {
    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        TravelerManager.updateTraveler(player)
    }

    // TODO add more Listener
}