package cafe.adriel.lyricist.sample.strings

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.AnnotatedString
import cafe.adriel.lyricist.sample.Locales

data class Strings(
    val simpleString: String,
    val annotatedString: AnnotatedString,
    val parameterString: (locale: String) -> String,
    val pluralString: (count: Int) -> String,
    val listStrings: List<String>
)

val strings = mapOf(
    Locales.EN to EnStrings,
    Locales.PT to PtStrings,
)

val LocalStrings = staticCompositionLocalOf { EnStrings }
