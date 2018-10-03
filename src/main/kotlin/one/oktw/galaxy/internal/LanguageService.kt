package one.oktw.galaxy.internal

import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ValueType
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.ConfigManager.Companion.config
import org.apache.commons.lang3.StringEscapeUtils
import java.nio.file.Files.createDirectory
import java.nio.file.Files.notExists
import java.nio.file.Paths
import java.util.*

class LanguageService(private val type: String? = null) {
    private val translationStorage = HashMap<String, ConfigurationNode>()

    fun getDefaultLanguage() = Translation(Locale.forLanguageTag(config.getNode("language").string))

    private fun substituteQuote(node: CommentedConfigurationNode) {
        when (node.valueType) {
            ValueType.LIST -> {
                if (node.hasListChildren()) {
                    node.childrenList.forEach { res ->
                        substituteQuote(res)
                    }
                }
            }
            ValueType.MAP -> {
                if (node.hasMapChildren()) {
                    node.childrenMap.forEach { (_, res) ->
                        substituteQuote(res)
                    }
                }
            }
            ValueType.SCALAR -> {
                if (node.value is String) {
                    val original = node.value as? String ?: return
                    if (original.startsWith("\"") and original.endsWith("\"")) {
                        val res = original
                            .let {
                                it.substring(1, it.length - 1)
                            }
                            .let {
                                StringEscapeUtils.unescapeJava(it)
                            }

                        node.value = res
                    }
                }
            }
            ValueType.NULL -> Unit
            else -> Unit
        }
    }

    init {
        // Init config
        config.getNode("language").let { if (it.isVirtual) it.value = "zh-TW" }

        //Init files
        if (type == null) {
            Paths.get(main.configDir.toString(), "lang")
        } else {
            Paths.get(main.configDir.toString(), "lang", type)

        }.let { if (notExists(it)) createDirectory(it) }

        Locale.getAvailableLocales().forEach { locale: Locale ->
            val asset = if (type == null) {
                main.plugin.getAsset("lang/${locale.toLanguageTag()}.lang")
            } else {
                main.plugin.getAsset("lang/$type/${locale.toLanguageTag()}.lang")
            }.orElse(null) ?: return@forEach

            HoconConfigurationLoader.builder()
                .setPath(
                    Paths.get(
                        main.configDir.toString(), if (type == null) {
                            "lang/${locale.toLanguageTag()}.cfg"
                        } else {
                            "lang/$type/${locale.toLanguageTag()}.cfg"
                        }
                    )
                )
                .build().run {
                    val node = if (canLoad()) load() else createEmptyNode()

                    save(
                        node.mergeValuesFrom(
                            HoconConfigurationLoader
                                .builder()
                                .setParseOptions(
                                    ConfigParseOptions.defaults().setSyntax(ConfigSyntax.PROPERTIES)
                                )
                                .setURL(
                                    asset.url
                                )
                                .build().load().apply {
                                    substituteQuote(this)
                                }
                        )
                    )

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
