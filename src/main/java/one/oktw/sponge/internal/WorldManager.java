package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.World;
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

    public void createWorld(String name) throws IOException {
        logger.info("Create World: " + name);
        try {
            server.createWorldProperties(name, worldArchetype);
        } catch (IOException e) {
            logger.error("Create world failed!", e);
            throw e;
        }
    }

    public void removeWorld(String name) {
        logger.info("Delete World: " + name);
        if (server.getWorldProperties(name).isPresent()) {
            if (server.getWorld(name).isPresent()) {
                World world = server.getWorld(name).get();
                world.getPlayers().forEach(player -> player.setLocationSafely(server.getWorld(server.getDefaultWorldName()).get().getSpawnLocation()));
                server.unloadWorld(world);
            }
            server.deleteWorld(server.getWorldProperties(name).get());
        } else {
            logger.error("World not found!");
        }
    }
}
