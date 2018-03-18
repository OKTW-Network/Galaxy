package one.oktw.galaxy

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.manager.*
import one.oktw.galaxy.register.CommandRegister
import one.oktw.galaxy.register.DataRegister
import one.oktw.galaxy.register.EventRegister
import one.oktw.galaxy.register.RecipeRegister
import one.oktw.galaxy.world.Planet
import org.slf4j.Logger
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameRegistryEvent
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameConstructionEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.event.game.state.GameStartingServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.world.gen.WorldGeneratorModifier

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@Plugin(
    id = "galaxy",
    name = "OKTW Galaxy",
    description = "OKTW Galaxy Project",
    version = "1.0-SNAPSHOT"
)
class Main {
    companion object {
        lateinit var main: Main
            private set
        lateinit var chunkLoaderManager: ChunkLoaderManager
            private set
        lateinit var commandManager: CommandRegister
            private set
        lateinit var configManager: ConfigManager
            private set
        lateinit var databaseManager: DatabaseManager
            private set
        lateinit var eventRegister: EventRegister
            private set
        lateinit var galaxyManager: GalaxyManager
            private set
        lateinit var travelerManager: TravelerManager
            private set
        lateinit var taskManager: TaskManager
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
    fun construct(event: GameConstructionEvent) {
        main = this
    }

    @Listener
    fun onRegister(event: GameRegistryEvent.Register<WorldGeneratorModifier>) {
        event.register(Planet())
    }

    @Listener
    fun onPreInit(event: GamePreInitializationEvent) {
        DataRegister()
        RecipeRegister()
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        logger.info("Initializing...")
        configManager = ConfigManager(configLoader)
        databaseManager = DatabaseManager()
        galaxyManager = GalaxyManager()
        travelerManager = TravelerManager()
        eventRegister = EventRegister()
        logger.info("Plugin initialized!")
    }

    @Listener
    fun onStarting(event: GameStartingServerEvent) {
        chunkLoaderManager = ChunkLoaderManager()

        CommandRegister()
        taskManager = TaskManager()
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }
}
