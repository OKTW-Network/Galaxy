package one.oktw.galaxy.player.event

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.saveMember
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.galaxy.planet.enums.AccessLevel.*
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.cleanPlayer
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.loadTraveler
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.saveTraveler
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.internal.ConfigManager.Companion.config
import one.oktw.galaxy.internal.ConfigManager.Companion.save
import one.oktw.galaxy.player.event.Viewer.Companion.isViewer
import one.oktw.galaxy.player.event.Viewer.Companion.removeViewer
import one.oktw.galaxy.player.event.Viewer.Companion.setViewer
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockTypes.END_PORTAL
import org.spongepowered.api.block.BlockTypes.PORTAL
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.CollideBlockEvent
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.game.state.GameStoppingServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.resourcepack.ResourcePacks
import org.spongepowered.api.service.user.UserStorageService
import java.net.URI
import java.util.Arrays.asList
import java.util.concurrent.TimeUnit

class PlayerControl {
    private val lobbyResourcePack: ResourcePack?
    private val planetResourcePack: ResourcePack?

    init {
        val config = config.getNode("resource-pack")

        if (config.getNode("lobby").isVirtual) config.getNode("lobby").setComment("Lobby ResourcePack")
        if (config.getNode("planet").isVirtual) config.getNode("planet").setComment("Planet ResourcePack")
        save()

        lobbyResourcePack = config.getNode("lobby").string?.let { ResourcePacks.fromUri(URI(it)) }
        planetResourcePack = config.getNode("planet").string?.let { ResourcePacks.fromUri(URI(it)) }

        // Auto save player data every 1 min
        launch(serverThread) {
            val server = Sponge.getServer()
            var players = server.onlinePlayers.iterator()

            while (true) {
                if (!players.hasNext()) {
                    players = server.onlinePlayers.iterator()
                    delay(1, TimeUnit.MINUTES)
                    continue
                }

                try {
                    val player = players.next()
                    val galaxy = galaxyManager.get(player.world) ?: continue

                    if (!player.isOnline) continue

                    galaxy.getMember(player.uniqueId)?.also {
                        galaxy.saveMember(saveTraveler(it, player))
                        delay(10, TimeUnit.SECONDS)
                    }
                } catch (e: RuntimeException) {
                    main.logger.error("Saving player data error", e)
                }
            }
        }
    }

    @Listener
    fun onAuth(event: ClientConnectionEvent.Auth) {
        val userService = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
        val server = Sponge.getServer()

        val user = userService.get(event.profile).orElse(null) ?: return

        // check world load else send to default world
        user.worldUniqueId.orElse(null)?.let(server::getWorld).run {
            if (this?.isPresent != true) {
                server.defaultWorld.get().run { user.setLocation(spawnPosition.toDouble(), uniqueId) }
            }
        }
    }

    @Listener
    fun onJoin(event: ClientConnectionEvent.Join, @Getter("getTargetEntity") player: Player) {
        // make player as viewer for safe
        setViewer(player.uniqueId)

        launch(serverThread) {
            val galaxy = galaxyManager.get(player.world)

            // restore player data
            galaxy?.getMember(player.uniqueId)?.let { loadTraveler(it, player) }

            // check permission for target planet
            when (galaxy?.getPlanet(player.world)?.checkPermission(player) ?: VIEW) {
                VIEW -> cleanPlayer(player)
                MODIFY -> {
                    removeViewer(player.uniqueId)
                    player.offer(Keys.GAME_MODE, player.world.properties.gameMode)
                }
                DENY -> player.transferToWorld(Sponge.getServer().run { getWorld(defaultWorldName).get() })
            }

            // send resource pack
            if (galaxy == null) lobbyResourcePack?.let(player::sendResourcePack) else planetResourcePack?.let(player::sendResourcePack)
        }
    }

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        if (isViewer(player.uniqueId)) return // skip viewer

        // save and clean player
        launch(serverThread) {
            galaxyManager.get(player.world)?.run {
                getMember(player.uniqueId)?.also { saveMember(saveTraveler(it, player)) }
            }
        }

        GUIHelper.closeAll(player)
    }

    @Listener
    fun onChangeWorld(event: MoveEntityEvent.Teleport, @Getter("getTargetEntity") player: Player) {
        if (event.fromTransform.extent == event.toTransform.extent) return

        // make player as viewer for safe
        setViewer(player.uniqueId)

        launch(serverThread) {
            val from = galaxyManager.get(event.fromTransform.extent)
            val to = galaxyManager.get(event.toTransform.extent)

            if (from?.uuid != to?.uuid) {
                // save and clean player data
                from?.getMember(player.uniqueId)?.also {
                    from.saveMember(saveTraveler(it, player))
                    cleanPlayer(player)
                } ?: cleanPlayer(player)

                // restore player data
                to?.let { galaxy -> galaxy.members.firstOrNull { it.uuid == player.uniqueId }?.also { loadTraveler(it, player) } }
            }

            // check permission
            when (to?.getPlanet(event.toTransform.extent)?.checkPermission(player) ?: VIEW) {
                VIEW -> Unit
                MODIFY -> {
                    removeViewer(player.uniqueId)
                    player.offer(Keys.GAME_MODE, event.toTransform.extent.properties.gameMode)
                }
                DENY -> player.transferToWorld(Sponge.getServer().run { getWorld(defaultWorldName).get() })
            }

            // send resource pack
            if (to == null) {
                lobbyResourcePack?.let(player::sendResourcePack)
            } else if (from == null) {
                planetResourcePack?.let(player::sendResourcePack)
            }
        }
    }

    @Listener
    fun disablePortal(event: CollideBlockEvent) {
        if (event.targetBlock.type in asList(PORTAL, END_PORTAL)) event.isCancelled = true
    }

    @Listener
    fun onServerStop(event: GameStoppingServerEvent) {
        Sponge.getServer().onlinePlayers.forEach { player ->
            runBlocking {
                galaxyManager.get(player.world)?.run { getMember(player.uniqueId)?.also { saveMember(saveTraveler(it, player)) } }
            }
        }
    }
}
