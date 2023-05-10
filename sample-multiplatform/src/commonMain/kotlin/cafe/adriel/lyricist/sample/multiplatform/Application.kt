package cafe.adriel.lyricist.sample.multiplatform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.rememberStrings
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.sample.multiplatform.strings.Strings

@Composable
internal fun SampleApplication() {
    val lyricist = rememberStrings()
    ProvideStrings(lyricist) {
        Column {
            SampleStrings(lyricist)

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SwitchLocaleButton(
                    lyricist,
                    Locales.EN,
                    Modifier.weight(1f)
                )
                Spacer(Modifier.weight(.1f))
                SwitchLocaleButton(
                    lyricist,
                    Locales.PT,
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
internal fun SampleStrings(lyricist: Lyricist<Strings>) {
    Column {
        Text(
            text = "Sample",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        // Simple simple
        Text(text = LocalStrings.current.simple)

        // Annotated string
        Text(text = LocalStrings.current.annotated)

        // Parameter string
        Text(text = LocalStrings.current.parameter(lyricist.languageTag))

        // Plural string
        Text(text = LocalStrings.current.plural(1))
        Text(text = LocalStrings.current.plural(5))
        Text(text = LocalStrings.current.plural(20))

        // List string
        Text(text = LocalStrings.current.list.joinToString())
    }
}

@Composable
internal fun SwitchLocaleButton(
    lyricist: Lyricist<Strings>,
    languageTag: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            lyricist.languageTag = languageTag
        },
        modifier = modifier
    ) {
        Text(text = languageTag)
    }
}
