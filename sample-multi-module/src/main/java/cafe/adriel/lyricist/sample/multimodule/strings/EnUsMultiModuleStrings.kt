package cafe.adriel.lyricist.sample.multimodule.strings

import cafe.adriel.lyricist.processor.Strings

@Strings(languageTag = "en-US", default = true)
val EnMultiModuleStrings = MultiModuleStrings(
    string = "Hello Compose!"
)
