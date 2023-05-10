package cafe.adriel.lyricist.sample.multiplatform.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings
import cafe.adriel.lyricist.sample.multiplatform.Locales

@LyricistStrings(languageTag = Locales.PT)
internal val PtStrings = Strings(
    simple = "Olá Compose!",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) { append("Olá ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append("Compose!") }
    },

    parameter = { locale ->
        "Localidade atual: $locale"
    },

    plural = { count ->
        val value = when (count) {
            1, 2 -> "poucas"
            in 3..10 -> "algumas"
            else -> "muitas"
        }
        "Eu tenho $value maças"
    },

    list = listOf("Abacate", "Abacaxi", "Ameixa")
)
