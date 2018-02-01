package one.oktw.galaxy.event

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.Main.Companion.viewerManager
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

class TravelerWatcher {
    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        travelerManager.updateTraveler(player)
        viewerManager.removeViewer(player.uniqueId)
    }
}