package one.oktw.galaxy

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.internal.CommandRegister
import one.oktw.galaxy.internal.ConfigManager
import one.oktw.galaxy.internal.DatabaseManager
import one.oktw.galaxy.internal.EventRegister
import one.oktw.galaxy.internal.galaxy.GalaxyManager
import one.oktw.galaxy.internal.galaxy.PlanetManager
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Path

@Plugin(id = "oktw-world",
        name = "OKTW World",
        description = "OKTW MultiWorld Project",
        dependencies = arrayOf(Dependency(id = "spotlin", optional = false, version = "0.1.3"))
)
class Main {
    companion object {
        @Inject
        val logger: Logger = null!!

        @Inject
        @DefaultConfig(sharedRoot = false)
        private val configLoader: ConfigurationLoader<CommentedConfigurationNode> = null!!

        @Inject
        @ConfigDir(sharedRoot = false)
        private val privatePluginDir: Path = null!!

        @Inject
        val plugin: PluginContainer = null!!

        var commandManager: CommandRegister
            private set
        var configManager: ConfigManager
            private set
        var databaseManager: DatabaseManager
            private set
        var eventRegister: EventRegister
            private set
        var planetManager: PlanetManager
            private set
        var galaxyManager: GalaxyManager
            private set
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        logger.info("Loading...")
        configManager = ConfigManager(configLoader)
        databaseManager = DatabaseManager()
        galaxyManager = GalaxyManager()
        planetManager = PlanetManager()
        eventRegister = EventRegister()
        commandManager = CommandRegister()
        Sponge.getServer().chunkTicketManager.registerCallback(this) { _, _ -> }
        logger.info("Plugin loaded!")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }

}
