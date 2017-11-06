package one.oktw.galaxy.event

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import one.oktw.galaxy.Main
import org.bson.Document
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ClientConnectionEvent
import java.util.*

class Disconnect {
    private val playerData = Main.databaseManager.database.getCollection("Player")

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect, @Getter("getTargetEntity") player: Player) {
        val location = player.location
        playerData.findOneAndUpdate(eq<UUID>("UUID", player.uniqueId), Document("\$set",
                Document("Location",
                        Document("x", location.x)
                                .append("y", location.y)
                                .append("z", location.z)
                )
                        .append("LastTime", Date())
        ), FindOneAndUpdateOptions().upsert(true))
    }
}
