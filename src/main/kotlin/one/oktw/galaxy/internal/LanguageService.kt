package one.oktw.galaxy.internal

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.Main.Companion.main
import java.nio.file.Files
import java.nio.file.Paths


class LanguageService(lang: String = "zh_TW") {
    private val langBuild: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
        .setPath(Paths.get(main.configDir.toString(), "lang/$lang.cfg")).build()
    private val rootNode: ConfigurationNode = langBuild.load()

    fun getString(key: String): String {
        return if (rootNode.getNode(key).string == null) key else rootNode.getNode(key).string
    }

    init {
        //Init Dir
        if (Files.notExists(Paths.get(main.configDir.toString(), "lang/"))) Files.createDirectory(Paths.get(main.configDir.toString(), "lang/"))
        //Init default languages
        if (Files.notExists(Paths.get(main.configDir.toString(), "lang/zh_TW.cfg"))) {
            main.plugin.getAsset("lang/zh_TW.cfg").get().copyToFile(Paths.get(main.configDir.toString(),"lang/zh_TW.cfg"))
        }
        if (Files.notExists(Paths.get(main.configDir.toString(), "lang/en_US.cfg"))) {
            main.plugin.getAsset("lang/en_US.cfg").get().copyToFile(Paths.get(main.configDir.toString(),"lang/en_US.cfg"))
        }
        //Init other languages base on English
        if (lang != "zh_TW" && lang != "en_US" && Files.notExists(Paths.get(main.configDir.toString(), "lang/$lang.cfg"))) {
            main.plugin.getAsset("lang/en_US.cfg").get().copyToFile(Paths.get(main.configDir.toString(),"lang/$lang.cfg"))
        }
    }
//    private fun saveLang() {langBuild.save(rootNode)}

}
