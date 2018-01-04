package one.oktw.galaxy.internal

import com.flowpowered.math.vector.Vector3d
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import one.oktw.galaxy.Main.Companion.databaseManager
import org.bson.Document
import org.spongepowered.api.entity.living.player.Player
import java.util.*

class PlayerInfoHelper {
    companion object {
        private val playerCollection = databaseManager.database.getCollection("Player")

        fun savePlayerInfo(player: Player) {
            val location = player.location
            playerCollection.findOneAndUpdate(eq<UUID>("UUID", player.uniqueId), Document("\$set",
                    Document("Planet", player.world.uniqueId)
                            .append("Position", Document("x", location.x).append("y", location.y).append("z", location.z))
                            .append("LastTime", Date())
            ), FindOneAndUpdateOptions().upsert(true))
        }

        fun getLastPlanet(uuid: UUID): UUID = playerCollection.find(eq("UUID", uuid)).first()["Planet"] as UUID

        fun getLastPosition(uuid: UUID): Vector3d {
            val pos = playerCollection.find(eq("UUID", uuid)).first()["Position"] as Document
            return Vector3d(pos["x"] as Double, pos["y"] as Double, pos["z"] as Double)
        }

        fun getLastTime(uuid: UUID): Date = playerCollection.find(eq("UUID", uuid)).first()["LastTime"] as Date
    }
}