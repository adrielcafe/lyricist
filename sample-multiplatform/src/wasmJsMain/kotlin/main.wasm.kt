import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.lyricist.sample.multiplatform.SampleApplication

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(
        title = "Voyager Sample",
        canvasElementId = "ComposeTarget"
    ) {
        SampleApplication()
    }
}
