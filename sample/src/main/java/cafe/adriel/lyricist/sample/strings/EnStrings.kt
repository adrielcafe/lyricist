package cafe.adriel.lyricist.sample.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

val EnStrings = Strings(
    simpleString = "Hello Compose!",

    annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red, fontFamily = FontFamily.Cursive)) { append("Hello ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light, fontSize = 16.sp)) { append("Compose!") }
    },

    parameterString = { locale ->
        "Current locale: $locale"
    },

    pluralString = { count ->
        val value = when (count) {
            1 -> "$count wish"
            else -> "$count wishes"
        }
        "You have $value remaining"
    },

    listStrings = listOf("Avocado", "Pineapple", "Plum", "Coconut")
)
