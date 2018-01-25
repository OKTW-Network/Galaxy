package one.oktw.galaxy.internal.types

import one.oktw.galaxy.internal.GalaxyManager
import one.oktw.galaxy.internal.PlanetManager
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import kotlin.collections.ArrayList

data class Galaxy(
        val uuid: UUID = UUID.randomUUID(),
        var name: String? = null,
        var members: List<Member> = ArrayList(),
        var planets: List<Planet> = ArrayList()
) {
    fun createPlanet(name: String) {
        val planet = PlanetManager.createPlanet(name)
        planets += planet

        GalaxyManager.saveGalaxy(this)
    }

    fun removePlanet(uuid: UUID) {
        val planet = planets.find { it.uuid === uuid } ?: return

        planet.world?.let { PlanetManager.removePlanet(it) }
    }

    fun savePlanet(planet: Planet) {
        planets = planets.map {
            if (it.uuid === planet.uuid) {
                return@map planet
            } else {
                return@map it
            }
        }

        GalaxyManager.saveGalaxy(this)
    }

    fun getGroup(player: Player): Groups {
        val member = members.find { it.uuid === player.uniqueId } ?: return Groups.VISITOR
        return member.group
    }
}
