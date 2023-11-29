package cafe.adriel.lyricist.processor.internal

import cafe.adriel.lyricist.processor.internal.ProcessorOptions.DEFAULT_LANGUAGE_TAG
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.DEFAULT_PACKAGE_NAME
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.DEFAULT_RESOURCES_FILENAME_FORMAT
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.GENERATE_STRINGS_PROPERTY
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.INTERNAL_VISIBILITY
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.LANGUAGE_TAG
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.MODULE_NAME
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.PACKAGE_NAME
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.RESOURCES_FILENAME_FORMAT
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.RESOURCES_PATH
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.SYMBOL_PROCESSOR_PATH
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.XML_DEFAULT_LANGUAGE_TAG
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.XML_RESOURCES_PATH
import cafe.adriel.lyricist.processor.internal.ProcessorOptions.availableProcessors
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import kotlin.reflect.full.primaryConstructor

internal class LyricistSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = with(environment) {
        val config = createConfig()
        val processors = findProcessors(config)

        LyricistSymbolProcessor(processors)
    }

    private fun SymbolProcessorEnvironment.createConfig() = LyricistConfig(
        packageName = options.getOrDefault(PACKAGE_NAME, DEFAULT_PACKAGE_NAME),
        moduleName = options[MODULE_NAME].orEmpty(),
        internalVisibility = options[INTERNAL_VISIBILITY].toBoolean(),
        defaultLanguageTag = getOptionAndWarnIfDeprecated(
            deprecatedKey = XML_DEFAULT_LANGUAGE_TAG,
            nonDeprecatedKey = LANGUAGE_TAG,
            defaultValue = DEFAULT_LANGUAGE_TAG
        ),
        resourcesPath = getOptionAndWarnIfDeprecated(
            deprecatedKey = XML_RESOURCES_PATH,
            nonDeprecatedKey = RESOURCES_PATH,
            defaultValue = "",
        ),
        resourcesFilenameFormat = options.getOrDefault(RESOURCES_FILENAME_FORMAT, DEFAULT_RESOURCES_FILENAME_FORMAT),
        generateStringsProperty = options[GENERATE_STRINGS_PROPERTY].toBoolean(),
    )

    private fun SymbolProcessorEnvironment.getOptionAndWarnIfDeprecated(
        deprecatedKey: String,
        nonDeprecatedKey: String,
        defaultValue: String,
    ): String {
        val value = options[deprecatedKey]
        if (value != null) {
            logger.warn(
                "Lyricist KSP argument \"$deprecatedKey\" is deprecated, " +
                        "use \"$nonDeprecatedKey\" instead."
            )
            return value
        }

        return options.getOrDefault(nonDeprecatedKey, defaultValue)
    }

    private fun SymbolProcessorEnvironment.findProcessors(config: LyricistConfig): List<SymbolProcessor> {
        val foundProcessors = mutableListOf<SymbolProcessor>()
        availableProcessors.forEach { processorName ->
            val className = SYMBOL_PROCESSOR_PATH.format(processorName.replaceFirstChar(Char::uppercaseChar))
            val processorClass = try {
                Class.forName(className)
            } catch (_: ClassNotFoundException) {
                logger.warn("Processor $className not found")
                return@forEach
            }

            val constructor = requireNotNull(processorClass.kotlin.primaryConstructor) {
                "Missing primary constructor(LyricistConfig, CodeGenerator, Logger) @ $processorClass"
            }
            val symbolProcessor = constructor.call(config, codeGenerator, logger) as SymbolProcessor

            foundProcessors.add(symbolProcessor)
            logger.warn("Processor $symbolProcessor loaded")
        }

        return foundProcessors.toList()
    }
}
