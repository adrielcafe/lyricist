package cafe.adriel.lyricist.sample.multiplatform

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.lyricist.sample.multiplatform.SampleApplication

public fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            SampleApplication()
        }
    }
}
