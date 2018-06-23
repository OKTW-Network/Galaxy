package one.oktw.galaxy.player.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.armor.ArmorHelper
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel.*
import one.oktw.galaxy.galaxy.traveler.TravelerHelper
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.cleanPlayer
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.getTraveler
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.saveTraveler
import one.oktw.galaxy.player.event.Viewer.Companion.removeViewer
import one.oktw.galaxy.player.event.Viewer.Companion.setViewer
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

class PlayerControl {
    @Listener
    fun onJoin(event: ClientConnectionEvent.Join, @Getter("getTargetEntity") player: Player) {
        launch {
            when (galaxyManager.get(player.world).await()?.getPlanet(player.world)?.checkPermission(player) ?: VIEW) {
                VIEW -> Viewer.setViewer(player.uniqueId)
                MODIFY -> Viewer.removeViewer(player.uniqueId)
                DENY -> player.transferToWorld(Sponge.getServer().run { getWorld(defaultWorldName).get() })
            }
        }
    }

    @Listener
    fun onChangeWorld(event: MoveEntityEvent.Teleport, @Getter("getTargetEntity") player: Player) {
        if (event.fromTransform.extent == event.toTransform.extent) return

        launch {
            val from = galaxyManager.get(event.fromTransform.extent).await()
            val to = galaxyManager.get(event.toTransform.extent).await()

            if (from?.uuid != to?.uuid) {
                // save player data
                from?.let { saveTraveler(player, true).await() } ?: cleanPlayer(player)

                // load player data
                to?.let {
                    getTraveler(player).await()?.also {
                        TravelerHelper.loadTraveler(it, player)
                        ArmorHelper.offerArmor(player)
                    }
                }
            }

            // check permission
            to?.also {
                when (it.getPlanet(event.toTransform.extent)?.checkPermission(player)) {
                    VIEW -> setViewer(player.uniqueId)
                    MODIFY -> removeViewer(player.uniqueId)
                    DENY -> player.transferToWorld(Sponge.getServer().run { getWorld(defaultWorldName).get() })
                }
            } ?: setViewer(player.uniqueId)
        }
    }
}
