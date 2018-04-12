package one.oktw.galaxy.galaxy.planet.data.extensions

import one.oktw.galaxy.Main
import one.oktw.galaxy.enums.AccessLevel
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.SecurityLevel
import one.oktw.galaxy.galaxy.data.extensions.getGroup
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.World
import java.util.*


suspend fun Planet.checkPermission(player: Player): AccessLevel {
    val group = Main.galaxyManager.getGalaxy(this).await().getGroup(player)

    return when (security) {
        SecurityLevel.MEMBER -> if (group != Group.VISITOR) AccessLevel.MODIFY else AccessLevel.DENY
        SecurityLevel.VISIT -> if (group != Group.VISITOR) AccessLevel.MODIFY else AccessLevel.VIEW
        SecurityLevel.PUBLIC -> AccessLevel.MODIFY
    }
}

fun Planet.loadWorld(): Optional<World> {
    return PlanetHelper.loadPlanet(this)
}
