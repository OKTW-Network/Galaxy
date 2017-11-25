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
import org.spongepowered.api.event.game.state.GameConstructionEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Path

@Plugin(id = "galaxy",
        name = "OKTW Galaxy",
        description = "OKTW Galaxy Project",
        dependencies = arrayOf(Dependency(id = "spotlin", optional = false, version = "0.1.3"))
)
class Main {
    companion object {
        lateinit var main: Main

        lateinit var commandManager: CommandRegister
            private set
        lateinit var configManager: ConfigManager
            private set
        lateinit var databaseManager: DatabaseManager
            private set
        lateinit var eventRegister: EventRegister
            private set
        lateinit var planetManager: PlanetManager
            private set
        lateinit var galaxyManager: GalaxyManager
            private set
    }

    @Inject lateinit var logger: Logger

    @Inject
    @DefaultConfig(sharedRoot = false)
    lateinit var configLoader: ConfigurationLoader<CommentedConfigurationNode>

    @Inject
    @ConfigDir(sharedRoot = false)
    lateinit var privatePluginDir: Path

    @Inject
    lateinit var plugin: PluginContainer

    @Listener
    fun construct(event: GameConstructionEvent) {
        main = this
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
        Sponge.getServer().chunkTicketManager.registerCallback(this) { _, _ -> } //TODO logger
        logger.info("Plugin loaded!")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }

}
