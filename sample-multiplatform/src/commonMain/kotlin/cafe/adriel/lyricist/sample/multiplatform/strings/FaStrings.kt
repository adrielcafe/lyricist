package cafe.adriel.lyricist.sample.multiplatform.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LayoutDirection
import cafe.adriel.lyricist.LyricistStrings
import cafe.adriel.lyricist.sample.multiplatform.Locales

@LyricistStrings(languageTag = Locales.FA, layoutDirection = LayoutDirection.Rtl)
internal val FaStrings = Strings(
    simple = "سلام کامپوز!",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) { append("سلام ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append("کامپوز!") }
    },

    parameter = { locale ->
        " زبان فعلی: $locale"
    },

    plural = { count ->
        val value = when (count) {
            0 -> "هیچی"
            1, 2 -> "یک مقدار"
            in 3..10 -> "تعدادی"
            else -> "خیلی"
        }
        " من $value سیب دارم. "
    },

    list = listOf("آووکادو", "آناناس", "آلو")
)
