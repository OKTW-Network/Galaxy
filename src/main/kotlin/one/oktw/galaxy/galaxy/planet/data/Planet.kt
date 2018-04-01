package one.oktw.galaxy.galaxy.planet.data

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.enums.AccessLevel
import one.oktw.galaxy.enums.AccessLevel.*
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.SecurityLevel
import one.oktw.galaxy.enums.SecurityLevel.*
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.World
import java.util.*

data class Planet @BsonCreator constructor(
    @BsonProperty("uuid") val uuid: UUID = UUID.randomUUID(),
    @BsonProperty("world") var world: UUID,
    @BsonProperty("name") var name: String,
    @BsonProperty("size") var size: Int = 32,
    @BsonProperty("security") var security: SecurityLevel = VISIT,
    @BsonProperty("lastTime") var lastTime: Date = Date()
) {
    suspend fun checkPermission(player: Player): AccessLevel {
        val group = galaxyManager.getGalaxy(this).await().getGroup(player)

        return when (security) {
            MEMBER -> if (group != Group.VISITOR) MODIFY else DENY
            VISIT -> if (group != Group.VISITOR) MODIFY else VIEW
            PUBLIC -> MODIFY
        }
    }

    fun loadWorld(): Optional<World> {
        return PlanetHelper.loadPlanet(this)
    }
}
