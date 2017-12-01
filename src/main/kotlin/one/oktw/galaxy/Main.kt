package one.oktw.galaxy

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.internal.*
import org.slf4j.Logger
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameConstructionEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Path

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
        eventRegister = EventRegister()
        commandManager = CommandRegister()
        chunkLoaderManager = ChunkLoaderManager()
        logger.info("Plugin loaded!")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }
}
