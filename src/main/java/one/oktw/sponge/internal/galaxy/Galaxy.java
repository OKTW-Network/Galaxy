package one.oktw.sponge.internal.galaxy;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import one.oktw.sponge.Main;
import org.bson.Document;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static java.util.stream.Collectors.toList;

public class Galaxy {
    private Main main = Main.getMain();
    private PlanetManager planetManager = main.getPlanetManager();
    private MongoDatabase database = main.getDatabaseManager().getDatabase();
    private MongoCollection<Document> galaxies = database.getCollection("Galaxy");
    private Document galaxy;

    Galaxy(UUID uuid) {
        galaxy = galaxies.find(eq("UUID", uuid)).first();
    }

    public UUID getUniqueId() {
        return (UUID) galaxy.get("UUID");
    }

    public String getName() {
        return galaxy.getString("Name");
    }

    public List<UUID> getMembers() {
        return ((List<Document>) galaxy.get("Members")).parallelStream().map(document -> ((UUID) document.get("UUID"))).collect(toList());
    }

    public List<UUID> getWorlds() {
        return (List<UUID>) galaxy.get("Worlds");
    }

    public Optional<World> createWorld(String name) {
        MongoCollection<Document> worlds = database.getCollection("World");
        if (worlds.find(eq("Name", name)).first() == null) {
            Planet planet = planetManager.createWorld(name);
            galaxies.findOneAndUpdate(
                    eq("UUID", getUniqueId()),
                    new Document("$push", new Document("Worlds", planet.getUniqueId()))
            );
            return planet.getWorld();
        } else {
            return Optional.empty();
        }
    }
}
