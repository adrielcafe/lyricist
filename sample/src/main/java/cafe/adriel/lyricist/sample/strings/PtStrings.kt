package cafe.adriel.lyricist.sample.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.processor.Strings
import cafe.adriel.lyricist.sample.Locales

@Strings(languageTag = Locales.PT)
val PtStrings = Strings(
    simpleString = "Olá Compose!",

    annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) { append("Olá ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append("Compose!") }
    },

    parameterString = { locale ->
        "Localidade atual: $locale"
    },

    pluralString = { count ->
        val value = when (count) {
            1 -> "apenas uma maça"
            2 -> "duas maças"
            in 3..10 -> "algumas maças"
            else -> "muitas maças"
        }
        "Eu tenho $value"
    },

    listStrings = listOf("Abacate", "Abacaxi", "Ameixa")
)
