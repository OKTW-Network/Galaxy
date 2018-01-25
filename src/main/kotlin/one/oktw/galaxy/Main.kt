package one.oktw.galaxy

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.internal.*
import org.slf4j.Logger
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameConstructionEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GameStartingServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer

@Plugin(id = "galaxy",
        name = "OKTW Galaxy",
        description = "OKTW Galaxy Project",
        version = "1.0-SNAPSHOT"
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
        lateinit var chunkLoaderManager: ChunkLoaderManager
            private set
    }

    @Inject
    lateinit var logger: Logger

    @Inject
    @DefaultConfig(sharedRoot = false)
    lateinit var configLoader: ConfigurationLoader<CommentedConfigurationNode>

    @Inject
    lateinit var plugin: PluginContainer

    @Listener
    fun construct(@SuppressWarnings("unused") event: GameConstructionEvent) {
        main = this
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        logger.info("Initialization...")
        configManager = ConfigManager(configLoader)
        databaseManager = DatabaseManager()
        eventRegister = EventRegister()
        chunkLoaderManager = ChunkLoaderManager()
        logger.info("Plugin initialized!")
    }

    @Listener
    fun onStarting(event: GameStartingServerEvent) {
        commandManager = CommandRegister()
        chunkLoaderManager.loadForcedWorld()
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }
}
