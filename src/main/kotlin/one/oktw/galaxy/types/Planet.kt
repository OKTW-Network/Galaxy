package one.oktw.galaxy.types

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.enums.AccessLevel
import one.oktw.galaxy.enums.AccessLevel.*
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.SecurityLevel
import one.oktw.galaxy.enums.SecurityLevel.*
import one.oktw.galaxy.helper.PlanetHelper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.World
import java.util.*

data class Planet(
        val uuid: UUID = UUID.randomUUID(),
        var world: UUID? = null,
        var name: String? = null,
        var size: Int = 32,
        var security: SecurityLevel = VISIT,
        var lastTime: Date = Date()
) {
    fun checkPermission(player: Player): AccessLevel {
        val group = galaxyManager.getGalaxy(this).getGroup(player)

        return when (security) {
            MEMBER -> if (group !== Group.VISITOR) MODIFY else DENY
            VISIT -> if (group !== Group.VISITOR) MODIFY else VIEW
            PUBLIC -> MODIFY
        }
    }

    fun loadWorld(): Optional<World> {
        return PlanetHelper.loadPlanet(this)
    }
}
