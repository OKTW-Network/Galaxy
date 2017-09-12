package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.WorldArchetype;

import java.io.IOException;

import static one.oktw.sponge.Main.getMain;

public class WorldManager {
    private Main core = getMain();
    private Logger logger = core.getLogger();
    private Server server = Sponge.getServer();
    private WorldArchetype worldArchetype = WorldArchetype.builder()
            .keepsSpawnLoaded(false)
            .generateSpawnOnLoad(false)
            .loadsOnStartup(false)
            .gameMode(GameModes.SURVIVAL)
            .randomSeed()
            .build("oktw-default", "OKTW Default");

    public void createWorld(String uuid) throws IOException {
        logger.info("Create World: " + uuid);
        try {
            server.createWorldProperties(uuid, worldArchetype);
        } catch (IOException e) {
            logger.error("Create world failed!", e);
            throw e;
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
