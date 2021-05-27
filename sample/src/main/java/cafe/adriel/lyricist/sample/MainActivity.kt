package cafe.adriel.lyricist.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import cafe.adriel.lyricist.sample.strings.Strings

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val lyricist = rememberStrings()

            ProvideStrings(lyricist) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    SampleStrings(lyricist)
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
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
    }

    @Composable
    fun SampleStrings(
        lyricist: Lyricist<Strings>
    ) {
        Column {
            // Simple simple
            Text(text = LocalStrings.current.simpleString)

            // Annotated string
            Text(text = LocalStrings.current.annotatedString)

            // Parameter string
            Text(text = LocalStrings.current.parameterString(lyricist.languageTag))

            // Plural string
            Text(text = LocalStrings.current.pluralString(2))
            Text(text = LocalStrings.current.pluralString(1))

            // List string
            Text(text = LocalStrings.current.listStrings.joinToString())
        }
    }

    @Composable
    fun SwitchLocaleButton(
        lyricist: Lyricist<Strings>,
        languageTag: String,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = { lyricist.languageTag = languageTag },
            modifier = modifier
        ) {
            Text(text = languageTag)
        }
    }
}
