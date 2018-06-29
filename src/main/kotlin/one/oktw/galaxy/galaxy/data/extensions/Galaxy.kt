package one.oktw.galaxy.galaxy.data.extensions

import one.oktw.galaxy.Main
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.World
import org.spongepowered.api.world.storage.WorldProperties
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

fun Galaxy.getPlanet(uuid: UUID) = planets.firstOrNull { it.uuid == uuid }

fun Galaxy.getPlanet(worldProperties: WorldProperties) = planets.firstOrNull { it.world == worldProperties.uniqueId }

fun Galaxy.getPlanet(world: World) = getPlanet(world.properties)

fun Galaxy.addMember(uuid: UUID, group: Group = MEMBER) {
    if (members.any { it.uuid == uuid }) return

    members.add(Traveler(uuid, group))
    save()
}

fun Galaxy.delMember(uuid: UUID) {
    members.remove(members.firstOrNull { it.uuid == uuid } ?: return)
    save()
}

fun Galaxy.saveMember(traveler: Traveler) {
    members.replaceAll { if (it.uuid == traveler.uuid) traveler else it }
}

fun Galaxy.getMember(uuid: UUID) = members.firstOrNull { it.uuid == uuid }

fun Galaxy.setGroup(uuid: UUID, group: Group) {
    members.first { it.uuid == uuid }.group = group
    save()
}

fun Galaxy.getGroup(player: Player): Group {
    return members.firstOrNull { it.uuid == player.uniqueId }?.group ?: VISITOR
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

fun Galaxy.dividends(number: Long) = takeStarDust(number * members.size).also {
    if (it) members.forEach { it.giveStarDust(number) }
}
