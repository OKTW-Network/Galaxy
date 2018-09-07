package one.oktw.galaxy.translation

import one.oktw.galaxy.internal.LanguageService
import one.oktw.i18n.api.provider.TranslationStringProvider
import java.util.*

class GalaxyTranslationProvider(private val languageService: LanguageService): TranslationStringProvider {
    override fun get(locale: Locale, key: String): String? {
        val result = languageService.Translation(locale)[key]

        if (result != key) {
            return result
        }

        // falling back to same languages that have different region...
        Locale.getAvailableLocales().forEach { tryLocale: Locale ->
            // try it to see if it works
            if (tryLocale.language == locale.language && tryLocale.variant == locale.variant) {
                val newResult = languageService.Translation(tryLocale)[key]
                if (newResult != key) {
                    return newResult
                }
            }
        }

        // falling back to same languages that have different variant...
        Locale.getAvailableLocales().forEach { tryLocale: Locale ->
            // try it to see if it works
            if (tryLocale.language == locale.language) {
                val newResult = languageService.Translation(tryLocale)[key]
                if (newResult != key) {
                    return newResult
                }
            }
        }

        // just give up
        return languageService.Translation(Locale.US)[key]
    }
}
