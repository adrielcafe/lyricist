package cafe.adriel.lyricist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale

public class Lyricist<T>(
    private val defaultLocale: Locale,
    private val translations: Map<Locale, T>
) {

    public var currentLocale: Locale by mutableStateOf(defaultLocale)

    public val strings: T
        get() = translations[currentLocale]
            ?: translations[currentLocale.fallback]
            ?: translations[defaultLocale]
            ?: error("Strings for locale ${currentLocale.toLanguageTag()} not found")

    private val Locale.fallback: Locale
        get() = toLanguageTag()
            .split(LANGUAGE_TAG_SEPARATOR)[0]
            .let(::Locale)

    public companion object {
        private const val LANGUAGE_TAG_SEPARATOR = '-'
    }
}

@Composable
public fun <T> rememberLyricist(
    vararg translations: Pair<Locale, T>,
    locale: Locale = Locale.current
): Lyricist<T> =
    rememberLyricist(translations.toMap(), locale)

@Composable
public fun <T> rememberLyricist(
    translations: Map<Locale, T>,
    locale: Locale = Locale.current
): Lyricist<T> =
    remember { Lyricist(locale, translations) }

@Composable
public fun <T> ProvideStrings(
    lyricist: Lyricist<T>,
    provider: ProvidableCompositionLocal<T>,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        provider provides lyricist.strings,
        content = content
    )
}
