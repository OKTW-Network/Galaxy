package one.oktw.galaxy.internal.types

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.internal.PlanetHelper
import one.oktw.galaxy.internal.enums.Group
import one.oktw.galaxy.internal.enums.Group.MEMBER
import java.util.*
import kotlin.collections.ArrayList

data class Galaxy(
        val uuid: UUID = UUID.randomUUID(),
        var name: String? = null,
        var members: List<Member> = ArrayList(),
        var planets: List<Planet> = ArrayList()
) {
    fun save() {
        galaxyManager.saveGalaxy(this)
    }

    fun createPlanet(name: String): Planet {
        val planet = PlanetHelper.createPlanet(name)
        planets += planet
        save()

        return planet
    }

    fun removePlanet(uuid: UUID) {
        val planet = planets.find { it.uuid === uuid } ?: return

        PlanetHelper.removePlanet(planet.world!!).thenAccept {
            if (it) planets -= planet
            save()
        }
    }

    fun addMember(uuid: UUID, group: Group = MEMBER) {
        if (members.any { it.uuid == uuid }) return

        members += Member(uuid, group)
        save()
    }

    fun delMember(uuid: UUID) {
        members -= members.find { it.uuid == uuid } ?: return
        save()
    }

    fun setMemberGroup(uuid: UUID, group: Group) {
        members.forEach { if (it.uuid == uuid) it.group = group }
        save()
    }

    fun getGroup(traveler: Traveler): Group {
        return members.find { it.uuid == traveler.uuid }?.group ?: return Group.VISITOR
    }
}
