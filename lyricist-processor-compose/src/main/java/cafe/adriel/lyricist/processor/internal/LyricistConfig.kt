package cafe.adriel.lyricist.processor.internal

internal data class LyricistConfig(
    val packageName: String,
    val moduleName: String,
    val internalVisibility: Boolean,
)
