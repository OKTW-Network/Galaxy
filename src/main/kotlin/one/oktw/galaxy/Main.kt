package one.oktw.galaxy

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.galaxy.GalaxyManager
import one.oktw.galaxy.galaxy.planet.gen.Planet
import one.oktw.galaxy.internal.DatabaseManager
import one.oktw.galaxy.internal.register.CommandRegister
import one.oktw.galaxy.internal.register.DataRegister
import one.oktw.galaxy.internal.register.EventRegister
import one.oktw.galaxy.internal.register.RecipeRegister
import one.oktw.galaxy.traveler.TravelerManager
import org.slf4j.Logger
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameRegistryEvent
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.*
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.world.gen.WorldGeneratorModifier

@Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
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
        lateinit var galaxyManager: GalaxyManager
            private set
        lateinit var travelerManager: TravelerManager
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
        DatabaseManager()
        galaxyManager = GalaxyManager()
        travelerManager = TravelerManager()
        EventRegister()
        logger.info("Plugin initialized!")
    }

    @Listener
    fun onStarting(event: GameStartingServerEvent) {
        CommandRegister()
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        //TODO
    }

    @Listener
    fun onStop(event: GameStoppedServerEvent) {
        galaxyManager.saveAll()
        travelerManager.saveAll()
    }
}
