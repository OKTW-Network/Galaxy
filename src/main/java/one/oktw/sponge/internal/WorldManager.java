package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.WorldArchetypes;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static one.oktw.sponge.Main.getMain;

public class WorldManager {
    private Main core = getMain();
    private Logger logger = core.getLogger();
    private Server server = Sponge.getServer();

    public void createWorld(String name) {
        logger.info("Create World [{}]", name);
        WorldArchetype worldArchetype;
        if (!Sponge.getGame().getRegistry().getType(WorldArchetype.class, name).isPresent()) {
            worldArchetype = WorldArchetype.builder()
                    .from(WorldArchetypes.OVERWORLD)
                    .keepsSpawnLoaded(false)
                    .generateSpawnOnLoad(false)
                    .loadsOnStartup(false)
                    .build(name, name);
        } else {
            worldArchetype = Sponge.getGame().getRegistry().getType(WorldArchetype.class, name).get();
        }

        try {
            server.createWorldProperties(name, worldArchetype);
        } catch (IOException e) {
            logger.error("Create world failed!", e);
        }
    }

    public void removeWorld(String name) {
        if (!server.getWorldProperties(name).isPresent()) {
            logger.error("Delete World [{}] failed: world not found", name);
        }

        logger.info("Deleting World [{}]", name);
        if (server.getWorld(name).isPresent()) {
            World world = server.getWorld(name).get();
            world.getPlayers().forEach(player -> player.setLocationSafely(server.getWorld(server.getDefaultWorldName()).get().getSpawnLocation()));
            server.unloadWorld(world);
        }

        try {
            server.deleteWorld(server.getWorldProperties(name).get()).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Delete world failed!", e);
        }
    }
}
