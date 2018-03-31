package one.oktw.galaxy.types

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.enums.Group.VISITOR
import one.oktw.galaxy.helper.PlanetHelper
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import kotlin.collections.ArrayList

data class Galaxy @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID = UUID.randomUUID(),
    @BsonProperty("name") var name: String,
    @BsonProperty("members") val members: ArrayList<Member> = ArrayList(),
    @BsonProperty("planets") val planets: ArrayList<Planet> = ArrayList(),
    @BsonProperty("joinRequest") val joinRequest: ArrayList<UUID> = ArrayList()
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

    fun requestJoin(uuid: UUID) {
        if (uuid in joinRequest) return

        joinRequest.add(uuid)
        save()
    }

    fun removeJoinRequest(uuid: UUID) {
        joinRequest.remove(uuid)
        save()
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
