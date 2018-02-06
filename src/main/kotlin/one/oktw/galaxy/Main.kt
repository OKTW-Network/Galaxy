package one.oktw.galaxy

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.data.DataOverheat
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.manager.*
import one.oktw.galaxy.task.CoolingStatus
import org.slf4j.Logger
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.data.DataRegistration
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameConstructionEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.event.game.state.GameStartingServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Task

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@Plugin(id = "galaxy",
        name = "OKTW Galaxy",
        description = "OKTW Galaxy Project",
        version = "1.0-SNAPSHOT"
)
class Main {
    companion object {
        lateinit var main: Main

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
        lateinit var viewerManager: ViewerManager
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
    fun onPreInit(event: GamePreInitializationEvent) {
        DataRegistration.builder()
                .dataName("UUID").manipulatorId("uuid")
                .dataClass(DataUUID::class.java).immutableClass(DataUUID.Immutable::class.java)
                .builder(DataUUID.Builder())
                .buildAndRegister(plugin)

        DataRegistration.builder()
                .dataName("Overheat").manipulatorId("overheat")
                .dataClass(DataOverheat::class.java).immutableClass(DataOverheat.Immutable::class.java)
                .builder(DataOverheat.Builder())
                .buildAndRegister(plugin)
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        logger.info("Initialization...")
        configManager = ConfigManager(configLoader)
        databaseManager = DatabaseManager()
        galaxyManager = GalaxyManager()
        travelerManager = TravelerManager()
        chunkLoaderManager = ChunkLoaderManager()
        eventRegister = EventRegister()
        logger.info("Plugin initialized!")
    }

    @Listener
    fun onStarting(event: GameStartingServerEvent) {
        commandManager = CommandRegister()
        viewerManager = ViewerManager()
        chunkLoaderManager.loadForcedWorld()

        Task.builder()
                .name("CoolingStatus")
                .intervalTicks(1)
                .execute(CoolingStatus())
                .submit(this)
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }
}
