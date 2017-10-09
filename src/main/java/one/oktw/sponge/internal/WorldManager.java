package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static one.oktw.sponge.Main.getMain;

public class WorldManager {
    private Main core = getMain();
    private Logger logger = core.getLogger();
    private Server server = Sponge.getServer();

    public Optional<World> createWorld(String name) {
        logger.info("Create World [{}]", name);
        WorldArchetype worldArchetype;
        if (!Sponge.getGame().getRegistry().getType(CatalogTypes.WORLD_ARCHETYPE, name).isPresent()) {
            worldArchetype = WorldArchetype.builder()
                    .keepsSpawnLoaded(false)
                    .generateSpawnOnLoad(false)
                    .loadsOnStartup(false)
                    .build(name, name);
        } else {
            worldArchetype = Sponge.getGame().getRegistry().getType(CatalogTypes.WORLD_ARCHETYPE, name).get();
        }

        try {
            WorldProperties properties = server.createWorldProperties(name, worldArchetype);
            properties.setKeepSpawnLoaded(false);
            properties.setGenerateSpawnOnLoad(false);
            properties.setLoadOnStartup(false);
        } catch (IOException e) {
            logger.error("Create world failed!", e);
        }

        return server.loadWorld(name);
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

    public Optional<World> loadWorld(String name) {
        Optional<WorldProperties> propertiesOptional = server.getWorldProperties(name);
        if (propertiesOptional.isPresent()) {
            WorldProperties worldProperties = propertiesOptional.get();
            worldProperties.setGenerateSpawnOnLoad(false);
            return server.loadWorld(worldProperties);
        } else {
            return Optional.empty();
        }
    }
}
