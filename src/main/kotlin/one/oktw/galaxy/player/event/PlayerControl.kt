package one.oktw.galaxy.player.event

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.saveMember
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel.*
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.cleanPlayer
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.loadTraveler
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.saveTraveler
import one.oktw.galaxy.player.event.Viewer.Companion.removeViewer
import one.oktw.galaxy.player.event.Viewer.Companion.setViewer
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.service.user.UserStorageService

class PlayerControl {
    @Listener
    fun onAuth(event: ClientConnectionEvent.Auth) {
        val userService = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
        val server = Sponge.getServer()

        val user = userService.get(event.profile).orElse(null) ?: return

        // check world load else send to default world
        user.worldUniqueId.orElse(null)?.let(server::getWorld)?.run {
            if (!isPresent) {
                server.defaultWorld.get().run {
                    user.setLocation(spawnPosition.toDouble(), uniqueId)
                }
            }
        }
    }

    @Listener
    fun onJoin(event: ClientConnectionEvent.Join, @Getter("getTargetEntity") player: Player) {
        launch {
            val galaxy = galaxyManager.get(player.world).await()

            // restore player data
            galaxy?.getMember(player.uniqueId)?.let { loadTraveler(it, player) }

            // check permission for target planet
            when (galaxy?.getPlanet(player.world)?.checkPermission(player) ?: VIEW) {
                VIEW -> {
                    Viewer.setViewer(player.uniqueId)
                    cleanPlayer(player)
                }
                MODIFY -> Viewer.removeViewer(player.uniqueId)
                DENY -> launch(serverThread) { player.transferToWorld(Sponge.getServer().run { getWorld(defaultWorldName).get() }) }
            }
        }
    }

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        // save and clean player
        launch {
            galaxyManager.get(player.world).await()?.run {
                getMember(player.uniqueId)?.also {
                    saveMember(saveTraveler(it, player).await())
                    cleanPlayer(player)
                }
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
                // save and clean player data
                from?.getMember(player.uniqueId)?.also {
                    from.saveMember(saveTraveler(it, player).await())
                    cleanPlayer(player).join()
                } ?: cleanPlayer(player).join()

                // load player data
                to?.let { it.members.firstOrNull { it.uuid == player.uniqueId }?.also { loadTraveler(it, player) } }

                // Set GameMode
                withContext(serverThread) { player.offer(Keys.GAME_MODE, event.toTransform.extent.properties.gameMode) }
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
