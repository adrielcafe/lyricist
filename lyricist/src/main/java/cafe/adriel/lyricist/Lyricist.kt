package cafe.adriel.lyricist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

public class Lyricist<T>(
    private val defaultLocale: LyricistLocale,
    private val translations: Map<LyricistLocale, T>
) {

    public var currentLocale: LyricistLocale by mutableStateOf(defaultLocale)

    public val strings: T
        get() = translations[currentLocale]
            ?: translations[currentLocale.fallbackOrNull]
            ?: translations[defaultLocale]
            ?: error("Strings for locale ${currentLocale.tag} not found")
}

@Composable
public fun <T> rememberLyricist(
    vararg translations: Pair<LyricistLocale, T>,
    locale: LyricistLocale = LyricistLocale.current
): Lyricist<T> =
    rememberLyricist(translations.toMap(), locale)

@Composable
public fun <T> rememberLyricist(
    translations: Map<LyricistLocale, T>,
    locale: LyricistLocale = LyricistLocale.current
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
