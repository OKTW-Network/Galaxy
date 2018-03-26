package one.oktw.galaxy.internal

import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.Main.Companion.main

import java.io.IOException

class ConfigManager(private val configLoader: ConfigurationLoader<CommentedConfigurationNode>) {
    private val logger = main.logger

    companion object {
        lateinit var config: CommentedConfigurationNode
            private set
    }

    init {
        try {
            config = configLoader.load()
        } catch (e: IOException) {
            logger.error("Config load error!", e)
        }

    }

    internal fun save() {
        try {
            configLoader.save(config)
        } catch (e: IOException) {
            logger.error("Config save error!", e)
        }
    }
}
