package one.oktw.galaxy.types

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.annotation.Document
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.helper.PlanetHelper
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import kotlin.collections.ArrayList

@Document
data class Galaxy(
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var members: ArrayList<Member> = ArrayList(),
    var planets: ArrayList<Planet> = ArrayList()
) {
    fun save() {
        galaxyManager.saveGalaxy(this)
    }

    fun createPlanet(name: String): Planet {
        val planet = PlanetHelper.createPlanet(name)
        planets.add(planet)
        save()

        return planet
    }

    fun removePlanet(uuid: UUID) {
        val planet = planets.firstOrNull { it.uuid == uuid } ?: return

        PlanetHelper.removePlanet(planet.world).thenAccept {
            if (it) planets.remove(planet)
            save()
        }
    }

    fun addMember(uuid: UUID, group: Group = MEMBER) {
        if (members.any { it.uuid == uuid }) return

        members.add(Member(uuid, group))
        save()
    }

    fun delMember(uuid: UUID) {
        members.remove(members.firstOrNull { it.uuid == uuid } ?: return)
        save()
    }

    fun setGroup(uuid: UUID, group: Group) {
        members.first { it.uuid == uuid }.group = group
        save()
    }

    fun getGroup(player: Player): Group {
        return members.firstOrNull { it.uuid == player.uniqueId }?.group ?: return VISITOR
    }
}
