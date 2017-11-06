package one.oktw.sponge.event;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import one.oktw.sponge.Main;
import org.bson.Document;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class Disconnect {
    private Main main = Main.getMain();
    private Logger logger = main.getLogger();
    private MongoCollection playerData = main.getDatabaseManager().getDatabase().getCollection("Player");

    @Listener
    public void onDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        Location<World> location = player.getLocation();
        playerData.findOneAndUpdate(eq("UUID", player.getUniqueId()), new Document("$set",
                new Document("Location",
                        new Document("x", location.getX())
                                .append("y", location.getY())
                                .append("z", location.getZ())
                )
                        .append("LastTime", new Date())
        ), new FindOneAndUpdateOptions().upsert(true));
    }
}
