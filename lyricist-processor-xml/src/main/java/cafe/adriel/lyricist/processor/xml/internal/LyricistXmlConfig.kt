package cafe.adriel.lyricist.processor.xml.internal

internal data class LyricistXmlConfig(
    val packageName: String,
    val moduleName: String,
    val defaultLanguageTag: String,
    val resourcesPath: String,
    val generateComposeAccessors: Boolean,
)
