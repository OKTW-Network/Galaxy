package one.oktw.galaxy.translation

import one.oktw.galaxy.internal.LanguageService
import one.oktw.i18n.api.provider.TranslationStringProvider
import java.util.*

class GalaxyTranslationProvider(private val languageService: LanguageService): TranslationStringProvider {
    override fun get(locale: Locale, key: String): String? {
        return languageService.Translation(locale)[key]
    }
}
