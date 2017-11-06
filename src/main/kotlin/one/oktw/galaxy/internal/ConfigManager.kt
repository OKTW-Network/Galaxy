package one.oktw.galaxy.internal

import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader

import java.io.IOException

class ConfigManager(configLoader: ConfigurationLoader<CommentedConfigurationNode>) {
    private val configLoader: ConfigurationLoader<*>
    internal var configNode: CommentedConfigurationNode? = null
        private set

    init {
        this.configLoader = configLoader
        try {
            configNode = configLoader.load()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    internal fun save() {
        try {
            configLoader.save(configNode)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
