package cafe.adriel.lyricist.sample.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.processor.Strings
import cafe.adriel.lyricist.sample.Locales

@Strings(languageTag = Locales.EN, default = true)
val EnStrings = Strings(
    simpleString = "Hello Compose!",

    annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) { append("Hello ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append("Compose!") }
    },

    parameterString = { locale ->
        "Current locale: $locale"
    },

    pluralString = { count ->
        val value = when (count) {
            1 -> "a single apple"
            2 -> "two apples"
            in 3..10 -> "a bunch of apples"
            else -> "a lot of apples"
        }
        "I have $value"
    },

    listStrings = listOf("Avocado", "Pineapple", "Plum")
)
