package one.oktw.galaxy.event

import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent

class ForceGamemode {
    @Listener
    fun onMoveEntityEventEventTeleport(event: MoveEntityEvent.Teleport, @Getter("getTargetEntity") player: Player) {
        val from = event.fromTransform.extent
        val to = event.toTransform.extent

        val properties = to.properties

        if (from != to) {
            if (!player.hasPermission("oktw.world.gamemode")) {
                if (properties.gameMode != player.gameMode().get()) {
                    player.offer<GameMode>(Keys.GAME_MODE, properties.gameMode)
                }
            }
        }
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join, @Getter("getTargetEntity") player: Player) {
        val properties = player.world.properties

        if (!player.hasPermission("oktw.world.gamemode")) {
            if (properties.gameMode != player.gameMode().get()) {
                player.offer<GameMode>(Keys.GAME_MODE, properties.gameMode)
            }
        }
    }
}
