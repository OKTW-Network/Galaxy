package one.oktw.galaxy.galaxy.data.extensions

import one.oktw.galaxy.Main
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.Member
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import org.spongepowered.api.entity.living.player.Player
import java.util.*

fun Galaxy.save() {
    Main.galaxyManager.saveGalaxy(this)
}

fun Galaxy.createPlanet(name: String): Planet {
    val planet = PlanetHelper.createPlanet(name)
    planets.add(planet)
    save()

    return planet
}

fun Galaxy.removePlanet(uuid: UUID) {
    val planet = planets.firstOrNull { it.uuid == uuid } ?: return

    PlanetHelper.removePlanet(planet.world).thenAccept {
        if (it) planets.remove(planet)
        save()
    }
}

fun Galaxy.addMember(uuid: UUID, group: Group = Group.MEMBER) {
    if (members.any { it.uuid == uuid }) return

    members.add(Member(uuid, group))
    save()
}

fun Galaxy.delMember(uuid: UUID) {
    members.remove(members.firstOrNull { it.uuid == uuid } ?: return)
    save()
}

fun Galaxy.setGroup(uuid: UUID, group: Group) {
    members.first { it.uuid == uuid }.group = group
    save()
}

fun Galaxy.getGroup(player: Player): Group {
    return members.firstOrNull { it.uuid == player.uniqueId }?.group ?: return Group.VISITOR
}

fun Galaxy.requestJoin(uuid: UUID) {
    if (uuid in joinRequest) return

    joinRequest.add(uuid)
    save()
}

fun Galaxy.removeJoinRequest(uuid: UUID) {
    joinRequest.remove(uuid)
    save()
}
