package one.oktw.sponge.internal.galaxy;

import com.mongodb.client.MongoDatabase;
import one.oktw.sponge.Main;
import org.bson.Document;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class Planet {
    private Server server = Sponge.getServer();
    private Main main = Main.getMain();
    private PlanetManager planetManager = main.getPlanetManager();
    private MongoDatabase database = main.getDatabaseManager().getDatabase();
    private Document planet;

    Planet(UUID uuid) {
        this.planet = database.getCollection("World").find(eq("UUID", uuid)).first();
    }

    public UUID getUniqueId() {
        return (UUID) planet.get("UUID");
    }

    public String getName() {
        return planet.getString("Name");
    }

    public int getSize() {
        return planet.getInteger("Size");
    }

    public Optional<World> getWorld() {
        return planetManager.loadWorld(getUniqueId());
    }

    public Optional<WorldProperties> getWorldProp() {
        return server.getWorldProperties(getUniqueId());
    }

    public int setSize(int size) {
        int originSize = getSize();
        WorldProperties properties = getWorldProp().get();

        database.getCollection("World").findOneAndUpdate(
                eq("UUID", getUniqueId()),
                new Document("$set", new Document("Size", size))
        );

        properties.setWorldBorderTargetDiameter(size * 16);
        server.saveWorldProperties(properties);

        return size - originSize;
    }

    public void setSecurity(int level) {
        database.getCollection("world").findOneAndUpdate(
                eq("UUID", getUniqueId()),
                new Document("$set", new Document("Security", level))
        );
    }
}
