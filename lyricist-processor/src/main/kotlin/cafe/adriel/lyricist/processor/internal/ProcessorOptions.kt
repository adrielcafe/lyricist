package cafe.adriel.lyricist.processor.internal

public object ProcessorOptions {

    public const val SYMBOL_PROCESSOR_PATH: String = "cafe.adriel.lyricist.processor.internal.Lyricist%sSymbolProcessor"

    // Common Options
    public const val PACKAGE_NAME: String = "lyricist.packageName"
    public const val MODULE_NAME: String = "lyricist.moduleName"
    public const val INTERNAL_VISIBILITY: String = "lyricist.internalVisibility"
    public const val LANGUAGE_TAG: String = "lyricist.defaultLanguageTag"
    public const val RESOURCES_PATH: String = "lyricist.resourcesPath"
    public const val RESOURCES_FILENAME_FORMAT: String = "lyricist.resourcesFilenameFormat"
    public const val GENERATE_STRINGS_PROPERTY: String = "lyricist.generateStringsProperty"

    public const val DEFAULT_PACKAGE_NAME: String = "cafe.adriel.lyricist"
    public const val DEFAULT_LANGUAGE_TAG: String = "en"
    public const val DEFAULT_RESOURCES_FILENAME_FORMAT: String = "messages_{lang}.properties"

    // XML Processor options
    public const val XML_RESOURCES_PATH: String = "lyricist.xml.resourcesPath"
    public const val XML_DEFAULT_LANGUAGE_TAG: String = "lyricist.xml.defaultLanguageTag"

    public val availableProcessors: List<String> = listOf("xml", "compose", "properties")
}
