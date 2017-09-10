package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.UUID;

import static one.oktw.sponge.Main.getMain;

public class WorldManager {
    private Main core = getMain();
    private Logger logger = core.getLogger();
    private Server server = Sponge.getServer();

    public void createWorld() {
        String uuid = UUID.randomUUID().toString();
        logger.info("Create World: " + uuid);
        try {
            WorldProperties worldProperties = server.createWorldProperties(uuid, WorldArchetype.builder()
                    .keepsSpawnLoaded(false)
                    .enabled(true)
                    .generateSpawnOnLoad(false)
                    .loadsOnStartup(false)
                    .randomSeed()
                    .build(uuid, uuid)
            );
        } catch (IOException e) {
            logger.error("Create world failed!", e);
        }
    }

    public void removeWorld(String uuid) {
        logger.info("Delete World: " + uuid);
        if (server.getWorldProperties(uuid).isPresent()) {
            server.deleteWorld(server.getWorldProperties(uuid).get());
        } else {
            logger.error("World not found!");
        }
    }
}
