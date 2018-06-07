package one.oktw.galaxy.internal

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.ConfigManager.Companion.config
import one.oktw.galaxy.internal.ConfigManager.Companion.save
import java.nio.file.Files.createDirectory
import java.nio.file.Files.notExists
import java.nio.file.Paths
import java.util.*

class LanguageService {
    private val translationStorage = HashMap<String, ConfigurationNode>()

    fun getDefaultLanguage() = Translation(Locale(config.getNode("language").string))

    init {
        val plugin = main.plugin
        // Init config 
        config.getNode("language").let { if (it.isVirtual) it.value = "zh_TW" }

        //Init files
        Paths.get(main.configDir.toString(), "lang").let { if (notExists(it)) createDirectory(it) }

        Locale.getAvailableLocales().forEach { locale: Locale ->
            val asset = plugin.getAsset("lang/${locale.toLanguageTag()}.cfg").orElse(null) ?: return@forEach

            HoconConfigurationLoader.builder()
                .setPath(Paths.get(main.configDir.toString(), "lang/${locale.toLanguageTag()}.cfg"))
                .build().run { if (canLoad()) load() else createEmptyNode() }
                .mergeValuesFrom(HoconConfigurationLoader.builder().setURL(asset.url).build().load())
                .apply { save() }
                .let { translationStorage[locale.toLanguageTag()] = it }
        }
    }

    inner class Translation(private val lang: Locale) {
        operator fun get(key: String, default: String? = null): String {
            return translationStorage[lang.toLanguageTag()]?.getNode(key)?.string ?: default ?: key
        }
    }
}
