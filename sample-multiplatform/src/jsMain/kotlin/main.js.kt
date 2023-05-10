import androidx.compose.ui.window.Window
import cafe.adriel.lyricist.sample.multiplatform.SampleApplication
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Voyager Sample") {
            SampleApplication()
        }
    }
}
