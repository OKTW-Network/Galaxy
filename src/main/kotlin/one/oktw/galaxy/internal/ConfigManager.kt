package one.oktw.galaxy.internal

import ninja.leaping.configurate.commented.CommentedConfigurationNode
import one.oktw.galaxy.Main.Companion.main

class ConfigManager {
    companion object {
        private val configLoader = main.configLoader
        var config: CommentedConfigurationNode = configLoader.load()
            private set

        fun save() = configLoader.save(config)

        fun reload() {
            config = configLoader.load()
        }
    }
}
