package one.oktw.galaxy.event

import one.oktw.galaxy.internal.PlayerInfoHelper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

class Disconnect {
    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        PlayerInfoHelper.savePlayerInfo(player)
    }
}
