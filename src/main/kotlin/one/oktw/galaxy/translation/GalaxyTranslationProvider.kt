/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
