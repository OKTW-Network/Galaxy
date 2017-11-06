package one.oktw.galaxy

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.sponge.internal.CommandRegister
import one.oktw.sponge.internal.ConfigManager
import one.oktw.sponge.internal.DatabaseManager
import one.oktw.sponge.internal.EventRegister
import one.oktw.sponge.internal.galaxy.GalaxyManager
import one.oktw.sponge.internal.galaxy.PlanetManager
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

@Plugin(id = "oktw-world",
        name = "OKTW World",
        description = "OKTW MultiWorld Project",
        dependencies = arrayOf(Dependency(id = "spotlin", optional = false, version = "0.1.3"))
)
class Main {

    @Inject
    val logger: Logger? = null

    @Inject
    @DefaultConfig(sharedRoot = false)
    private val configLoader: ConfigurationLoader<CommentedConfigurationNode>? = null

    @Inject
    @ConfigDir(sharedRoot = false)
    private val privatePluginDir: Path? = null

    @Inject
    val plugin: PluginContainer? = null

    private lateinit var commandManager: CommandRegister
    private lateinit var configManager: ConfigManager
    private lateinit var databaseManager: DatabaseManager
    private lateinit var eventRegister: EventRegister
    private lateinit var planetManager: PlanetManager
    private lateinit var galaxyManager: GalaxyManager

    @Listener
    fun construct(event: GameConstructionEvent) {
        main = this
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        logger!!.info("Loading...")
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

    companion object {
        var main: Main? = null
            private set
    }
}
