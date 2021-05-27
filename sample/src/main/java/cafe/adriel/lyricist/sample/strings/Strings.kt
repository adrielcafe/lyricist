package cafe.adriel.lyricist.sample.strings

import androidx.compose.ui.text.AnnotatedString

data class Strings(
    val simpleString: String,
    val annotatedString: AnnotatedString,
    val parameterString: (locale: String) -> String,
    val pluralString: (count: Int) -> String,
    val listStrings: List<String>
)
