package cafe.adriel.lyricist.sample

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalXmlStrings
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.ProvideXmlStrings
import cafe.adriel.lyricist.XmlStrings
import cafe.adriel.lyricist.custompackage.LocalMultiModuleStrings
import cafe.adriel.lyricist.custompackage.ProvideMultiModuleStrings
import cafe.adriel.lyricist.custompackage.rememberMultiModuleStrings
import cafe.adriel.lyricist.getLocaleStrings
import cafe.adriel.lyricist.rememberStrings
import cafe.adriel.lyricist.rememberXmlStrings
import cafe.adriel.lyricist.sample.multimodule.strings.MultiModuleStrings
import cafe.adriel.lyricist.sample.strings.Strings
import cafe.adriel.lyricist.strings

class MainActivity : ComponentActivity() {

    private lateinit var lyricist: Lyricist<Strings>
    private lateinit var multiModuleLyricist: Lyricist<MultiModuleStrings>
    private lateinit var xmlLyricist: Lyricist<XmlStrings>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            lyricist = rememberStrings()
            multiModuleLyricist = rememberMultiModuleStrings()
            xmlLyricist = rememberXmlStrings()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                LazyColumn {
                    item {
                        ProvideStrings(lyricist) {
                            SampleStrings()
                        }
                    }

                    item {
                        ProvideMultiModuleStrings(multiModuleLyricist) {
                            SampleMultiModuleStrings()
                        }
                    }

                    item {
                        ProvideXmlStrings(xmlLyricist) {
                            SampleXmlStrings()
                        }
                    }

                    item {
                        Row(modifier = Modifier.padding(vertical = 16.dp)) {
                            ShowToastButton()
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    SwitchLocaleButton(
                        Locales.EN,
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.weight(.1f))
                    SwitchLocaleButton(
                        Locales.PT,
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }

    @Composable
    fun SampleStrings() {
        Column {
            Text(
                text = "Sample",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Simple simple
            Text(text = strings.simple)

            // Annotated string
            Text(text = strings.annotated)

            // Parameter string
            Text(text = strings.parameter(lyricist.languageTag))

            // Plural string
            Text(text = strings.plural(0))
            Text(text = strings.plural(1))
            Text(text = strings.plural(5))
            Text(text = strings.plural(20))

            // List string
            Text(text = strings.list.joinToString())
        }
    }

    @Composable
    fun SampleMultiModuleStrings() {
        Column {
            Text(
                text = "MultiModule Sample",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Simple simple
            Text(text = LocalMultiModuleStrings.current.string)
        }
    }

    @Composable
    fun SampleXmlStrings() {
        Column {
            Text(
                text = "XML Sample",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Simple simple
            Text(text = LocalXmlStrings.current.simple)

            // Parameter string
            Text(text = LocalXmlStrings.current.params("A", 1, "B", 2))

            // Plural string
            Text(text = LocalXmlStrings.current.plurals(0))
            Text(text = LocalXmlStrings.current.plurals(1))
            Text(text = LocalXmlStrings.current.plurals(2))
            Text(text = LocalXmlStrings.current.plurals(3))

            // List string
            Text(text = LocalXmlStrings.current.array.joinToString())
        }
    }

    @Composable
    fun ShowToastButton(
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = {
                showToast()
            }, modifier = modifier
        ) {
            Text(text = "Show Toast")
        }
    }

    private fun showToast() {
        Toast.makeText(this, getLocaleStrings().nonComposeAlert, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun SwitchLocaleButton(
        languageTag: String,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = {
                lyricist.languageTag = languageTag
                multiModuleLyricist.languageTag = languageTag
                xmlLyricist.languageTag = languageTag
            },
            modifier = modifier
        ) {
            Text(text = languageTag)
        }
    }
}
