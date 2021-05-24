package cafe.adriel.lyricist.sample.strings

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.AnnotatedString
import cafe.adriel.lyricist.LyricistLocale

data class Strings(
    val simpleString: String,
    val annotatedString: AnnotatedString,
    val parameterString: (locale: String) -> String,
    val pluralString: (count: Int) -> String,
    val listStrings: List<String>
)

val strings = mapOf(
    LyricistLocale.EN to EnStrings,
    LyricistLocale.PT to PtStrings,
)

val LocalStrings = staticCompositionLocalOf { EnStrings }
