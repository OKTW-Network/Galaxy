package one.oktw.galaxy.internal.types

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.internal.PlanetHelper
import one.oktw.galaxy.internal.enums.AccessLevel
import one.oktw.galaxy.internal.enums.AccessLevel.*
import one.oktw.galaxy.internal.enums.Group
import one.oktw.galaxy.internal.enums.SecurityLevel
import one.oktw.galaxy.internal.enums.SecurityLevel.*
import java.util.*

data class Planet(
        val uuid: UUID = UUID.randomUUID(),
        var world: UUID? = null,
        var name: String? = null,
        var size: Int = 32,
        var security: SecurityLevel = VISIT,
        var lastTime: Date = Date()
) {
    fun save() {
        PlanetHelper.updatePlanet(this)
        galaxyManager.getGalaxy(this).save()
    }

    fun checkPermission(traveler: Traveler): AccessLevel {
        val group = galaxyManager.getGalaxy(this).getGroup(traveler)

        return when (security) {
            MEMBER -> if (group !== Group.VISITOR) MODIFY else DENY
            VISIT -> if (group !== Group.VISITOR) MODIFY else VIEW
            PUBLIC -> MODIFY
        }
    }
}
