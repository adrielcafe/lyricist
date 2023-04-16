package cafe.adriel.lyricist.sample.multiplatform

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.lyricist.sample.multiplatform.SampleApplication

public class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SampleApplication()
        }
    }
}
