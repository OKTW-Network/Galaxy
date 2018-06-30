package one.oktw.galaxy

import com.google.inject.Inject
import kotlinx.coroutines.experimental.CloseableCoroutineDispatcher
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.galaxy.GalaxyManager
import one.oktw.galaxy.galaxy.planet.gen.PlanetGenModifier
import one.oktw.galaxy.internal.DatabaseManager
import one.oktw.galaxy.internal.LanguageService
import one.oktw.galaxy.internal.register.CommandRegister
import one.oktw.galaxy.internal.register.DataRegister
import one.oktw.galaxy.internal.register.EventRegister
import one.oktw.galaxy.internal.register.RecipeRegister
import one.oktw.galaxy.machine.chunkloader.ChunkLoaderManager
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
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
import java.nio.file.Path
import java.util.*

@Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@Plugin(
    id = "galaxy",
    name = "OKTW Galaxy",
    description = "OKTW Galaxy Project",
    version = "1.0-SNAPSHOT"
)
class Main {
    companion object {
        val dummyUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

        lateinit var main: Main
            private set
        lateinit var serverThread: CloseableCoroutineDispatcher
            private set
        lateinit var galaxyManager: GalaxyManager
            private set
        lateinit var chunkLoaderManager: ChunkLoaderManager
            private set
        lateinit var languageService: LanguageService
            private set
    }

    @Inject
    lateinit var logger: Logger

    @Inject
    @DefaultConfig(sharedRoot = false)
    lateinit var configLoader: ConfigurationLoader<CommentedConfigurationNode>

    @Inject
    @ConfigDir(sharedRoot = false)
    lateinit var configDir: Path

    @Inject
    lateinit var plugin: PluginContainer

    @Listener
    fun construct(event: GameConstructionEvent) {
        main = this
    }

    @Listener
    fun onRegister(event: GameRegistryEvent.Register<WorldGeneratorModifier>) {
        event.register(PlanetGenModifier())
    }

    @Listener
    fun onPreInit(event: GamePreInitializationEvent) {
        serverThread = Sponge.getScheduler().createSyncExecutor(this).asCoroutineDispatcher()
        languageService = LanguageService()
        DataRegister()
        RecipeRegister()
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        logger.info("Initializing...")
        DatabaseManager()
        galaxyManager = GalaxyManager()
        EventRegister()
        logger.info("Plugin initialized!")
    }

    @Listener
    fun onStarting(event: GameStartingServerEvent) {
        chunkLoaderManager = ChunkLoaderManager()
        CommandRegister()
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }
}
