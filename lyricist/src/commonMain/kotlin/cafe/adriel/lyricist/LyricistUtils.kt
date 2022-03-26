package cafe.adriel.lyricist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale

public typealias LanguageTag = String

@Composable
public fun <T> rememberStrings(
    translations: Map<LanguageTag, T>,
    languageTag: LanguageTag = Locale.current.toLanguageTag()
): Lyricist<T> =
    remember { Lyricist(languageTag, translations) }

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
