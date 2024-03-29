package cafe.adriel.lyricist.processor.xml.internal

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class LyricistXmlSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        with(environment) {
            LyricistXmlSymbolProcessor(
                config = LyricistXmlConfig(
                    packageName = options[ARG_PACKAGE_NAME] ?: DEFAULT_PACKAGE_NAME,
                    moduleName = options[ARG_MODULE_NAME].orEmpty(),
                    defaultLanguageTag = options[ARG_DEFAULT_LANGUAGE_TAG] ?: DEFAULT_LANGUAGE_TAG,
                    resourcesPath = options[ARG_RESOURCES_PATH]
                        ?: throw IllegalArgumentException("lyricist.xml.resourcesPath not found"),
                    generateComposeAccessors = options[ARG_GENERATE_COMPOSE_ACCESSORS]?.toBoolean() ?: true
                ),
                codeGenerator = codeGenerator,
                logger = logger
            )
        }

    private companion object {
        const val ARG_PACKAGE_NAME = "lyricist.packageName"
        const val ARG_MODULE_NAME = "lyricist.xml.moduleName"
        const val ARG_RESOURCES_PATH = "lyricist.xml.resourcesPath"
        const val ARG_DEFAULT_LANGUAGE_TAG = "lyricist.xml.defaultLanguageTag"
        const val ARG_GENERATE_COMPOSE_ACCESSORS = "lyricist.xml.generateComposeAccessors"

        const val DEFAULT_PACKAGE_NAME = "cafe.adriel.lyricist"
        const val DEFAULT_LANGUAGE_TAG = "en"
    }
}
