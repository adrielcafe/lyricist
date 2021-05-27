package cafe.adriel.lyricist.processor.internal

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class LyricistSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ): SymbolProcessor =
        LyricistSymbolProcessor(options.moduleName, codeGenerator, logger)

    private val Map<String, String>.moduleName: String
        get() = get(ARG_MODULE_NAME).orEmpty()

    private companion object {
        const val ARG_MODULE_NAME = "lyricist.moduleName"
    }
}
