package one.oktw.galaxy.event

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.Main.Companion.viewerManager
import one.oktw.galaxy.helper.CoolDownHelper.Companion.getCoolDown
import one.oktw.galaxy.helper.CoolDownHelper.Companion.removeCoolDown
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

class TravelerWatcher {
    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        travelerManager.getTraveler(player).item.forEach { getCoolDown(it.uuid)?.let { it1 -> removeCoolDown(it1) } }
        travelerManager.updateTraveler(player)
        viewerManager.removeViewer(player.uniqueId)
    }
}