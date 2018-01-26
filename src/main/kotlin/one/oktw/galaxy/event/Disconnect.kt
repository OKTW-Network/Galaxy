package one.oktw.galaxy.event

import one.oktw.galaxy.internal.TravelerManager
import one.oktw.galaxy.internal.types.Position
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

class Disconnect {
    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        val traveler = TravelerManager.getTraveler(player)
        traveler.position = Position().fromPosition(player.location.position)
        traveler.save()
    }
}
