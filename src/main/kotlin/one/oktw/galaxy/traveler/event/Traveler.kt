package one.oktw.galaxy.traveler.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.enums.AccessLevel.*
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.traveler.ViewerHelper
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

@Suppress("unused", "UNUSED_PARAMETER")
class Traveler {
    @Listener
    fun onJoin(event: ClientConnectionEvent.Join, @Getter("getTargetEntity") player: Player) {
        launch {
            val planet = galaxyManager.getPlanetFromWorld(player.world.uniqueId).await() ?: return@launch
            when (planet.checkPermission(player)) {
                MODIFY -> Unit
                VIEW -> ViewerHelper.setViewer(player.uniqueId)
                DENY -> Sponge.getServer().defaultWorld.ifPresent {
                    player.transferToWorld(
                        it.uniqueId,
                        it.spawnPosition.toDouble()
                    )
                }
            }
        }
    }

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        launch {
            travelerManager.updateTraveler(player)
            ViewerHelper.removeViewer(player.uniqueId)
        }
    }
}
