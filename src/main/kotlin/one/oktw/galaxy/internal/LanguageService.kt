package one.oktw.galaxy.internal

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.ConfigManager.Companion.config
import java.nio.file.Files.createDirectory
import java.nio.file.Files.notExists
import java.nio.file.Paths
import java.util.*

class LanguageService private constructor() {
    companion object {
        private val instance = LanguageService()

        fun getInstance() = instance
    }

    private val translationStorage = HashMap<String, ConfigurationNode>()

    fun getDefaultLanguage() = Translation(Locale.forLanguageTag(config.getNode("language").string))

    init {
        // Init config
        config.getNode("language").let { if (it.isVirtual) it.value = "zh-TW" }

        //Init files
        Paths.get(main.configDir.toString(), "lang").let { if (notExists(it)) createDirectory(it) }

        Locale.getAvailableLocales().forEach { locale: Locale ->
            val asset = main.plugin.getAsset("lang/${locale.toLanguageTag()}.cfg").orElse(null) ?: return@forEach

            HoconConfigurationLoader.builder()
                .setPath(Paths.get(main.configDir.toString(), "lang/${locale.toLanguageTag()}.cfg"))
                .build().run {
                    val node = if (canLoad()) load() else createEmptyNode()

                    save(node.mergeValuesFrom(HoconConfigurationLoader.builder().setURL(asset.url).build().load()))

                    translationStorage[locale.toLanguageTag()] = node
                }
        }
    }

    inner class Translation(private val lang: Locale) {
        operator fun get(key: String, default: String? = null) = get(key.split('.'), default)

        operator fun get(key: List<String>, default: String? = null) = get(key.toTypedArray(), default)

        operator fun get(key: Array<String>, default: String? = null): String {
            return translationStorage[lang.toLanguageTag()]?.getNode(*key)?.getString(default) ?: key.joinToString(".")
        }
    }
}
