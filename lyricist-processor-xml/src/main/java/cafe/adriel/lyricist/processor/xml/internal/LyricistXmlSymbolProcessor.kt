package cafe.adriel.lyricist.processor.xml.internal

import cafe.adriel.lyricist.processor.xml.internal.ktx.INDENTATION
import cafe.adriel.lyricist.processor.xml.internal.ktx.filterXmlStringFiles
import cafe.adriel.lyricist.processor.xml.internal.ktx.formatted
import cafe.adriel.lyricist.processor.xml.internal.ktx.getXmlStrings
import cafe.adriel.lyricist.processor.xml.internal.ktx.languageTag
import cafe.adriel.lyricist.processor.xml.internal.ktx.normalized
import cafe.adriel.lyricist.processor.xml.internal.ktx.params
import com.fleshgrinder.extensions.kotlin.toLowerCamelCase
import com.fleshgrinder.extensions.kotlin.toUpperCamelCase
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File

internal class LyricistXmlSymbolProcessor(
    private val config: LyricistXmlConfig,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val strings = mutableMapOf<LanguageTag, StringResources>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        strings += File(config.resourcesPath)
            .walk()
            .filterXmlStringFiles()
            .groupBy { file ->
                val languageTag = file.languageTag

                if (languageTag.isBlank()) {
                    config.defaultLanguageTag
                } else {
                    languageTag
                }
            }
            .mapValues { (_, files) ->
                files.map { it.getXmlStrings() }
                    .flatten()
                    .toMap()
            }
        return emptyList()
    }

    override fun finish() {
        if (strings.isEmpty()) return

        val fileName = "${config.moduleName.toUpperCamelCase()}Strings"

        val stringsName = "${config.moduleName.toLowerCamelCase()}Strings"

        strings[config.defaultLanguageTag]
            ?.let { writeStringsClassFile(fileName, stringsName, it, strings.keys) }
            ?: logger.error("Default language tag not found")

        strings.forEach { (languageTag, strings) ->
            writeStringsPropertyFile(fileName, languageTag, strings)
        }
    }

    private fun writeStringsClassFile(
        fileName: String,
        stringsName: String,
        strings: StringResources,
        languageTags: Set<String>
    ) {
        val values = strings
            .map { (key, value) ->
                val type = when (value) {
                    is StringResource.PlainString -> {
                        val params = value.value.params
                        if (params.isEmpty()) {
                            "String"
                        } else {
                            "(${params.joinToString()}) -> String"
                        }
                    }
                    is StringResource.StringArray -> "List<String>"
                    is StringResource.Plurals -> "(quantity: Int) -> String"
                }
                "val ${key.toLowerCamelCase()}: $type"
            }
            .joinToString("\n") { "$INDENTATION$it" }

        val translationMappingOutput = languageTags
            .map { languageTag ->
                languageTag to "${languageTag.toUpperCamelCase()}$fileName"
            }.joinToString(",\n") { (languageTag, property) ->
                "${INDENTATION}Locales.${languageTag.toUpperCamelCase()} to $property"
            }

        val localesOutput = languageTags
            .joinToString("\n") { languageTag ->
                "${INDENTATION}val ${languageTag.toUpperCamelCase()} = \"$languageTag\""
            }

        val defaultStringsOutput = "${config.defaultLanguageTag.toUpperCamelCase()}$fileName"

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true),
            packageName = config.packageName,
            fileName = fileName
        ).use { stream ->
            stream.write(
                """
                |package ${config.packageName}
                |
                |import androidx.compose.runtime.Composable
                |import androidx.compose.runtime.ProvidableCompositionLocal
                |import androidx.compose.runtime.staticCompositionLocalOf
                |import androidx.compose.ui.text.intl.Locale
                |import cafe.adriel.lyricist.Lyricist
                |import cafe.adriel.lyricist.LanguageTag
                |import cafe.adriel.lyricist.rememberStrings
                |import cafe.adriel.lyricist.ProvideStrings
                |
                |public interface $fileName {
                |$values
                |}
                |
                |public object Locales {
                |$localesOutput
                |}
                |
                |public val $stringsName: Map<LanguageTag, $fileName> = mapOf(
                |$translationMappingOutput
                |)
                |
                |public val Local$fileName: ProvidableCompositionLocal<$fileName> = 
                |    staticCompositionLocalOf { $defaultStringsOutput }
                |
                |@Composable
                |public fun remember$fileName(
                |    defaultLanguageTag: LanguageTag = "${config.defaultLanguageTag}",
                |    currentLanguageTag: LanguageTag = Locale.current.toLanguageTag(),
                |): Lyricist<$fileName> =
                |    rememberStrings($stringsName, defaultLanguageTag, currentLanguageTag)
                |
                |@Composable
                |public fun Provide$fileName(
                |    lyricist: Lyricist<$fileName>,
                |    content: @Composable () -> Unit
                |) {
                |    ProvideStrings(lyricist, Local$fileName, content)
                |}
                |
                |public fun getLocale$fileName(locale: Locale = Locale.current): $fileName {
                |    return $stringsName[locale.toLanguageTag()] ?: $defaultStringsOutput
                |}
                """.trimMargin().toByteArray()
            )
        }
    }

    private fun writeStringsPropertyFile(fileName: String, languageTag: String, strings: StringResources) {
        val propertyName = languageTag.toUpperCamelCase() + fileName

        val values = strings
            .map { (key, value) ->
                val resourceValue = when (value) {
                    is StringResource.PlainString -> {
                        val typedParams = value.value.params
                            .mapIndexed { i, type -> "p$i: $type" }
                            .joinToString()
                        val params = value.value.params
                            .mapIndexed { i, _ -> "p$i" }
                            .joinToString()

                        if (params.isEmpty()) {
                            "\"${value.value.normalized}\""
                        } else {
                            """{ $typedParams -> 
                            |        "${value.value.normalized}"
                            |            .format($params)
                            |    }
                            """.trimMargin()
                        }
                    }

                    is StringResource.StringArray ->
                        """listOf(
                        |${value.value.formatted}
                        |    )
                        """.trimMargin()

                    is StringResource.Plurals ->
                        """{ quantity: Int ->
                        |        when (quantity) {
                        |${value.value.formatted}
                        |        }.format(quantity)
                        |    }
                        """.trimMargin()
                }
                "override val ${key.toLowerCamelCase()} = $resourceValue"
            }
            .joinToString("\n\n") { "$INDENTATION$it" }

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true),
            packageName = config.packageName,
            fileName = propertyName
        ).use { stream ->
            stream.write(
                """
                |package ${config.packageName}
                |
                |val $propertyName = object : $fileName {
                |  $values
                |}
                """.trimMargin().toByteArray()
            )
        }
    }
}
