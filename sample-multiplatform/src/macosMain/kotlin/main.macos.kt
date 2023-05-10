import androidx.compose.ui.window.Window
import cafe.adriel.lyricist.sample.multiplatform.SampleApplication
import platform.AppKit.NSApp
import platform.AppKit.NSApplication

fun main() {
    NSApplication.sharedApplication()
    Window("VoyagerMultiplatform") {
        SampleApplication()
    }
    NSApp?.run()
}

