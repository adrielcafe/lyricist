package cafe.adriel.lyricist.sample.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.processor.Strings
import cafe.adriel.lyricist.sample.Locales

@Strings(languageTag = Locales.PT)
val PtStrings = Strings(
    simpleString = "Olá Compose!",

    annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red, fontFamily = FontFamily.Cursive)) { append("Olá ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light, fontSize = 16.sp)) { append("Compose!") }
    },

    parameterString = { locale ->
        "Localidade atual: $locale"
    },

    pluralString = { count ->
        val value = when (count) {
            1 -> "$count desejo"
            else -> "$count desejos"
        }
        "Você tem $value restando"
    },

    listStrings = listOf("Abacate", "Abacaxi", "Ameixa", "Coco")
)
