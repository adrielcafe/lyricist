package cafe.adriel.lyricist.sample.multiplatform

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

public fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            SampleApplication()
        }
    }
}
