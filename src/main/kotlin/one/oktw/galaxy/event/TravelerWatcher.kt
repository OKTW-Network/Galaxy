package one.oktw.galaxy.event

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.Main.Companion.viewerManager
import one.oktw.galaxy.enums.AccessLevel.*
import one.oktw.galaxy.helper.CoolDownHelper.Companion.getCoolDown
import one.oktw.galaxy.helper.CoolDownHelper.Companion.removeCoolDown
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

@Suppress("unused", "UNUSED_PARAMETER")
class TravelerWatcher {
    @Listener
    fun onJoin(event: ClientConnectionEvent.Join, @Getter("getTargetEntity") player: Player) {
        val planet = galaxyManager.getPlanetFromWorld(player.world.uniqueId) ?: return
        when (planet.checkPermission(player)) {
            MODIFY -> return
            VIEW -> viewerManager.setViewer(player.uniqueId)
            DENY -> Sponge.getServer().defaultWorld.ifPresent { player.transferToWorld(it.uniqueId, it.spawnPosition.toDouble()) }
        }
    }

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        travelerManager.getTraveler(player).item.forEach { getCoolDown(it.uuid)?.let { it1 -> removeCoolDown(it1) } }
        travelerManager.updateTraveler(player)
        viewerManager.removeViewer(player.uniqueId)
    }
}