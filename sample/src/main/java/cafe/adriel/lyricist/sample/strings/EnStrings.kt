package cafe.adriel.lyricist.sample.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings
import cafe.adriel.lyricist.sample.Locales

@LyricistStrings(languageTag = Locales.EN, default = true)
internal val EnStrings = Strings(
    simple = "Hello Compose!",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) { append("Hello ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append("Compose!") }
    },

    parameter = { locale ->
        "Current locale: $locale"
    },

    plural = { count ->
        val value = when (count) {
            0 -> "no"
            1, 2 -> "a few"
            in 3..10 -> "a bunch of"
            else -> "a lot of"
        }
        "I have $value apples"
    },

    list = listOf("Avocado", "Pineapple", "Plum"),

    nonComposeAlert = "This is a sample toast",
)
