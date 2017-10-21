package one.oktw.sponge;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import one.oktw.sponge.internal.CommandRegister;
import one.oktw.sponge.internal.ConfigManager;
import one.oktw.sponge.internal.DatabaseManager;
import one.oktw.sponge.internal.EventRegister;
import one.oktw.sponge.internal.galaxy.GalaxyManager;
import one.oktw.sponge.internal.galaxy.PlanetManager;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

@Plugin(id = "oktw-world", name = "OKTW World", description = "OKTW MultiWorld Project")
public class Main {
    private static Main main;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privatePluginDir;

    @Inject
    private PluginContainer plugin;

    private CommandRegister commandRegister;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private EventRegister eventRegister;
    private PlanetManager planetManager;
    private GalaxyManager galaxyManager;

    public static Main getMain() {
        return main;
    }

    @Listener
    public void construct(GameConstructionEvent event) {
        main = this;
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        logger.info("Loading...");
        configManager = new ConfigManager(configLoader);
        databaseManager = new DatabaseManager();
        galaxyManager = new GalaxyManager();
        planetManager = new PlanetManager();
        eventRegister = new EventRegister();
        commandRegister = new CommandRegister();
        logger.info("Plugin loaded!");
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        //TODO
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public CommandRegister getCommandManager() {
        return commandRegister;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EventRegister getEventRegister() {
        return eventRegister;
    }

    public PlanetManager getPlanetManager() {
        return planetManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GalaxyManager getGalaxyManager() {
        return galaxyManager;
    }
}
