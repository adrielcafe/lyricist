package cafe.adriel.lyricist.processor.internal

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class LyricistSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        with(environment) {
            LyricistSymbolProcessor(
                config = LyricistConfig(
                    packageName = options[ARG_PACKAGE_NAME] ?: DEFAULT_PACKAGE_NAME,
                    moduleName = options[ARG_MODULE_NAME].orEmpty(),
                    internalVisibility = options[ARG_INTERNAL_VISIBILITY].toBoolean(),
                    generateStringsProperty = options[ARG_GENERATE_STRINGS_PROPERTY].toBoolean()
                ),
                codeGenerator = codeGenerator,
                logger = logger
            )
        }

    private companion object {
        const val ARG_PACKAGE_NAME = "lyricist.packageName"
        const val ARG_MODULE_NAME = "lyricist.moduleName"
        const val ARG_INTERNAL_VISIBILITY = "lyricist.internalVisibility"
        const val ARG_GENERATE_STRINGS_PROPERTY = "lyricist.generateStringsProperty"

        const val DEFAULT_PACKAGE_NAME = "cafe.adriel.lyricist"
    }
}
