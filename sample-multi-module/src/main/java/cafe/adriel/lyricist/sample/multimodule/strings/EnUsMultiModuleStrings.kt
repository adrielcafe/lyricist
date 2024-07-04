package cafe.adriel.lyricist.sample.multimodule.strings

import cafe.adriel.lyricist.LayoutDirection
import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "en", layoutDirection = LayoutDirection.Rtl, default = true)
val EnMultiModuleStrings = MultiModuleStrings(
    string = "Hello Compose!"
)
