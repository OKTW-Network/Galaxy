package one.oktw.galaxy.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.enums.AccessLevel.*
import one.oktw.galaxy.helper.CoolDownHelper.Companion.getCoolDown
import one.oktw.galaxy.helper.CoolDownHelper.Companion.removeCoolDown
import one.oktw.galaxy.helper.ViewerHelper
import one.oktw.galaxy.types.item.Overheat
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

@Suppress("unused", "UNUSED_PARAMETER")
class TravelerWatcher {
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
            travelerManager.getTraveler(player).item
                .filter { it is Overheat }
                .forEach { getCoolDown((it as Overheat).uuid)?.let { removeCoolDown(it) } }
            travelerManager.updateTraveler(player)
            ViewerHelper.removeViewer(player.uniqueId)
        }
    }
}
