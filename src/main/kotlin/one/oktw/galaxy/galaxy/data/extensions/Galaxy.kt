package one.oktw.galaxy.galaxy.data.extensions

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import one.oktw.galaxy.galaxy.traveler.extensions.getPlayer
import one.oktw.galaxy.player.event.Viewer.Companion.setViewer
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.RED
import org.spongepowered.api.world.World
import org.spongepowered.api.world.storage.WorldProperties
import java.util.*

private suspend fun Galaxy.save() {
    galaxyManager.saveGalaxy(this)
}

suspend fun Galaxy.refresh() = galaxyManager.get(uuid)!!

fun Galaxy.update(block: Galaxy.() -> Unit) = launch {
    block()
    refresh().also(block).save()
}

suspend fun Galaxy.createPlanet(name: String): Planet {
    val planet = PlanetHelper.createPlanet(name)
    update { planets.add(planet) }

    return planet
}

fun Galaxy.removePlanet(uuid: UUID) {
    val planet = planets.firstOrNull { it.uuid == uuid } ?: return

    PlanetHelper.removePlanet(planet.world).thenAccept {
        if (it) update { planets.remove(planet) }
    }
}

fun Galaxy.getPlanet(uuid: UUID) = planets.firstOrNull { it.uuid == uuid }

fun Galaxy.getPlanet(worldProperties: WorldProperties) = planets.firstOrNull { it.world == worldProperties.uniqueId }

fun Galaxy.getPlanet(world: World) = getPlanet(world.properties)

fun Galaxy.addMember(uuid: UUID, group: Group = MEMBER) = update {
    if (members.any { it.uuid == uuid }) return@update

    members.add(Traveler(uuid, group))
}

fun Galaxy.delMember(uuid: UUID) {
    val member = members.firstOrNull { it.uuid == uuid } ?: return
    val planet = getPlanet(member.getPlayer()?.world!!)
    if (planet != null) {
        member.getPlayer()
            ?.sendMessage(Text.of(RED, languageService.getDefaultLanguage()["traveler.memberRemovedNotice"]))
        if (planet.visitable) setViewer(uuid)
    }
    update { members.remove(members.firstOrNull { it.uuid == uuid } ?: return@update) }
}

fun Galaxy.saveMember(traveler: Traveler) = update {
    members.replaceAll { if (it.uuid == traveler.uuid) traveler else it }
}

fun Galaxy.getMember(uuid: UUID) = members.firstOrNull { it.uuid == uuid }

fun Galaxy.setGroup(uuid: UUID, group: Group) = update { members.first { it.uuid == uuid }.group = group }

fun Galaxy.getGroup(player: Player) = members.firstOrNull { it.uuid == player.uniqueId }?.group ?: VISITOR

fun Galaxy.requestJoin(uuid: UUID) = update {
    if (uuid in joinRequest) return@update

    joinRequest.add(uuid)
}

fun Galaxy.removeJoinRequest(uuid: UUID) = update { joinRequest.remove(uuid) }

fun Galaxy.dividends(number: Long) = update {
    takeStarDust(number * members.size).also {
        if (it) members.forEach { it.giveStarDust(number) }
    }
}
