package cafe.adriel.lyricist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection as ComposeLayoutDirection

@Composable
public fun <T> rememberStrings(
    translations: Map<LanguageTag, T>,
    layoutDirections: Map<LanguageTag, LayoutDirection>,
    defaultLanguageTag: LanguageTag = "en",
    currentLanguageTag: LanguageTag = Locale.current.toLanguageTag()
): Lyricist<T> =
    remember(defaultLanguageTag) {
        Lyricist(defaultLanguageTag, layoutDirections, translations)
    }.apply {
        languageTag = currentLanguageTag
    }

@Composable
public fun <T> ProvideStrings(
    lyricist: Lyricist<T>,
    provider: ProvidableCompositionLocal<T>,
    content: @Composable () -> Unit
) {
    val state by lyricist.state.collectAsState()

    CompositionLocalProvider(
        provider provides state.strings,
        LocalLayoutDirection provides state.layoutDirection.toComposeLayoutDirection(),
        content = content
    )
}

private fun LayoutDirection.toComposeLayoutDirection(): ComposeLayoutDirection {
    return when (this) {
        LayoutDirection.Ltr -> ComposeLayoutDirection.Ltr
        LayoutDirection.Rtl -> ComposeLayoutDirection.Rtl
    }
}
