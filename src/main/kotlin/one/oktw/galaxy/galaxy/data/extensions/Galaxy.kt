package one.oktw.galaxy.galaxy.data.extensions

import kotlinx.coroutines.future.await
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import one.oktw.galaxy.galaxy.traveler.extensions.getPlayer
import one.oktw.galaxy.player.event.Viewer.Companion.setViewer
import one.oktw.galaxy.translation.extensions.toLegacyText
import org.spongepowered.api.Sponge
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

suspend fun Galaxy.update(block: Galaxy.() -> Unit) {
    block()
    refresh().also(block).save()
}

suspend fun Galaxy.createPlanet(name: String, type: PlanetType = PlanetType.NORMAL): Planet {
    val planet = PlanetHelper.createPlanet(name, type)
    update { planets.add(planet) }

    return planet
}

suspend fun Galaxy.removePlanet(uuid: UUID) {
    val planet = planets.firstOrNull { it.uuid == uuid } ?: return

    if (PlanetHelper.removePlanet(planet.world).await()) {
        update { planets.remove(planet) }
    }
}

fun Galaxy.getPlanet(uuid: UUID) = planets.firstOrNull { it.uuid == uuid }

fun Galaxy.getPlanet(worldProperties: WorldProperties) = planets.firstOrNull { it.world == worldProperties.uniqueId }

fun Galaxy.getPlanet(world: World) = getPlanet(world.properties)

suspend fun Galaxy.addMember(uuid: UUID, group: Group = MEMBER) {
    if (members.any { it.uuid == uuid }) return

    update { members.add(Traveler(uuid, group)) }
}

suspend fun Galaxy.delMember(uuid: UUID) {
    val lang = Main.translationService
    val member = members.firstOrNull { it.uuid == uuid } ?: return

    member.getPlayer()?.run {
        val planet = getPlanet(world)

        if (planet != null) {
            sendMessage(Text.of(RED, lang.of("traveler.memberRemovedNotice")).toLegacyText(this))

            if (planet.visitable) {
                setViewer(uuid)
            } else {
                transferToWorld(Sponge.getServer().run { getWorld(defaultWorldName).get() })
            }
        }
    }

    update { members.remove(members.first { it.uuid == uuid }) }
}

suspend fun Galaxy.saveMember(traveler: Traveler) = update {
    members.replaceAll { if (it.uuid == traveler.uuid) traveler else it }
}

fun Galaxy.getMember(uuid: UUID) = members.firstOrNull { it.uuid == uuid }

suspend fun Galaxy.setGroup(uuid: UUID, group: Group) = update { members.first { it.uuid == uuid }.group = group }

fun Galaxy.getGroup(player: Player) = members.firstOrNull { it.uuid == player.uniqueId }?.group ?: VISITOR

suspend fun Galaxy.requestJoin(uuid: UUID) {
    if (uuid in joinRequest) return

    update { joinRequest.add(uuid) }
}

suspend fun Galaxy.removeJoinRequest(uuid: UUID) = update { joinRequest.remove(uuid) }

suspend fun Galaxy.dividends(number: Long): Boolean {
    if (starDust < number * members.size) return false

    update {
        takeStarDust(number * members.size)
        members.forEach { it.giveStarDust(number) }
    }

    return true
}
