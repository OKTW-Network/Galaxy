package one.oktw.galaxy.galaxy.traveler

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.galaxy.data.extensions.getMember
import org.spongepowered.api.entity.living.player.Player

class TravelerHelper {
    companion object {
        suspend fun getTraveler(player: Player) = galaxyManager.get(player.world).await()?.getMember(player.uniqueId)
    }
}
