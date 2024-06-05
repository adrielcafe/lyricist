package cafe.adriel.lyricist.sample.strings

import androidx.compose.ui.text.AnnotatedString

internal data class Strings(
    val simple: String,
    val annotated: AnnotatedString,
    val parameter: (locale: String) -> String,
    val plural: (count: Int) -> String,
    val list: List<String>,
    val nonComposeAlert:String
)
