package one.oktw.sponge.internal.galaxy;

import com.mongodb.client.MongoCollection;
import one.oktw.sponge.Main;
import org.bson.Document;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.mongodb.client.model.Filters.eq;
import static one.oktw.sponge.Main.getMain;
import static one.oktw.sponge.internal.galaxy.SecurityLevel.VISIT;

public class PlanetManager {
    private Main main = getMain();
    private Logger logger = main.getLogger();
    private MongoCollection<Document> collection = main.getDatabaseManager().getDatabase().getCollection("World");
    private Server server = Sponge.getServer();

    Planet createWorld(String name) {
        WorldProperties properties;
        logger.info("Create World [{}]", name);

        try {
            properties = server.createWorldProperties(name, WorldArchetypes.OVERWORLD);
            properties.setKeepSpawnLoaded(false);
            properties.setGenerateSpawnOnLoad(false);
            properties.setLoadOnStartup(false);
            server.saveWorldProperties(properties);
        } catch (IOException e) {
            logger.error("Create world failed!", e);
            throw new UncheckedIOException(e);
        }

        Document worldInfo = new Document("UUID", properties.getUniqueId())
                .append("Name", name)
                .append("Size", 32)
                .append("Security", VISIT);

        collection.insertOne(worldInfo);
        return new Planet(properties.getUniqueId());
    }

    public void removeWorld(UUID uuid) {
        WorldProperties properties;
        if (server.getWorldProperties(uuid).isPresent()) {
            properties = server.getWorldProperties(uuid).get();
        } else {
            logger.error("Delete World [{}] failed: world not found", uuid.toString());
            return;
        }

        logger.info("Deleting World [{}]", properties.getWorldName());
        if (server.getWorld(uuid).isPresent()) {
            World world = server.getWorld(uuid).get();
            world.getPlayers().forEach(player -> player.setLocationSafely(server.getWorld(server.getDefaultWorldName()).get().getSpawnLocation()));
            server.unloadWorld(world);
        }

        try {
            server.deleteWorld(properties).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Delete world failed!", e);
            return;
        }

        collection.deleteOne(eq("UUID", uuid));
    }

    Optional<World> loadWorld(UUID uuid) {
        if (server.getWorldProperties(uuid).isPresent()) {
            WorldProperties worldProperties = server.getWorldProperties(uuid).get();
            worldProperties.setGenerateSpawnOnLoad(false);
            return server.loadWorld(worldProperties);
        } else {
            return Optional.empty();
        }
    }
}
