package cafe.adriel.lyricist.processor.internal

import com.fleshgrinder.extensions.kotlin.toLowerCamelCase
import com.fleshgrinder.extensions.kotlin.toUpperCamelCase
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.validate

internal class LyricistSymbolProcessor(
    private val config: LyricistConfig,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val declarations = mutableListOf<KSPropertyDeclaration>()
    private val previousRoundDeclarations = mutableSetOf<KSPropertyDeclaration>()

    private val visitor = LyricistVisitor(declarations)
    private var generatedInPreviousRounds: Boolean = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        declarations.clear()
        val lyricistSymbols = resolver.getSymbolsWithAnnotation(ANNOTATION_PACKAGE)
            .filter { it is KSPropertyDeclaration && it.validate() }
            .toList()

        lyricistSymbols.forEach { it.accept(visitor, Unit) }

        val roundDeclaration =
            declarations.filterNot { dec -> previousRoundDeclarations.any { it.qualifiedName == dec.qualifiedName } }
        previousRoundDeclarations += declarations

        if (validate(roundDeclaration).not()) return emptyList()

        val fileName = "${config.moduleName.toUpperCamelCase()}Strings"

        val stringsName = "${config.moduleName.toLowerCamelCase()}Strings"

        val visibility = if (config.internalVisibility) "internal" else "public"

        val stringsProperty = if (config.generateStringsProperty) {
            """
            |$visibility val strings: $fileName
            |    @Composable
            |    get() = Local$fileName.current
            """.trimMargin()
        } else {
            ""
        }

        val defaultLanguageTag = roundDeclaration
            .firstNotNullOfOrNull { it.annotations.getDefaultLanguageTag() }
            ?.let { "\"$it\"" }
            ?: "Locale.current.toLanguageTag()"

        val defaultStrings = roundDeclaration
            .first { it.annotations.getValue<Boolean>(ANNOTATION_PARAM_DEFAULT) == true }

        val packagesOutput = roundDeclaration
            .mapNotNull { it.qualifiedName?.asString() }
            .plus(defaultStrings.getClassQualifiedName())
            .joinToString(separator = "\n") { packageName -> "import $packageName" }

        val stringsClassOutput = defaultStrings.getClassSimpleName()

        val defaultStringsOutput = defaultStrings.simpleName.getShortName()

        val translationMappingOutput = roundDeclaration
            .map {
                it.annotations.getValue<String>(ANNOTATION_PARAM_LANGUAGE_TAG)!! to it.simpleName.getShortName()
            }.joinToString(",\n") { (languageTag, property) ->
                "$INDENTATION\"$languageTag\" to $property"
            }

        codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = true,
                sources = roundDeclaration.map { it.containingFile!! }.toTypedArray()
            ),
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
                |$packagesOutput
                |
                |$visibility val $stringsName: Map<LanguageTag, $stringsClassOutput> = mapOf(
                |$translationMappingOutput
                |)
                |
                |$visibility val Local$fileName: ProvidableCompositionLocal<$stringsClassOutput> = 
                |    staticCompositionLocalOf { $defaultStringsOutput }
                |
                |$stringsProperty
                |
                |@Composable
                |$visibility fun remember$fileName(
                |    defaultLanguageTag: LanguageTag = $defaultLanguageTag,
                |    currentLanguageTag: LanguageTag = Locale.current.toLanguageTag(),
                |): Lyricist<$stringsClassOutput> =
                |    rememberStrings($stringsName, defaultLanguageTag, currentLanguageTag)
                |
                |@Composable
                |$visibility fun Provide$fileName(
                |    lyricist: Lyricist<$stringsClassOutput> = remember$fileName(),
                |    content: @Composable () -> Unit
                |) {
                |    ProvideStrings(lyricist, Local$fileName, content)
                |}
                |
                |$visibility fun getLocale$fileName(locale: Locale = Locale.current): $stringsClassOutput {
                |    return $stringsName[locale.toLanguageTag()] ?: $defaultStringsOutput
                |}                
                """.trimMargin().toByteArray()
            )
        }

        generatedInPreviousRounds = true

        return emptyList()
    }

    private fun validate(properties: List<KSPropertyDeclaration>): Boolean {
        val defaultCount = properties
            .count { it.annotations.getValue<Boolean>(ANNOTATION_PARAM_DEFAULT) == true }

        val differentTypeCount = properties
            .groupBy { it.getClassQualifiedName() }
            .count()

        return when {
            generatedInPreviousRounds -> {
                // ignore code generation if is a new round and did already generated previously
                false
            }

            defaultCount == 0 -> {
                logger.warn("No @LyricistStrings(default = true) found")
                false
            }

            defaultCount > 1 -> {
                logger.exception(IllegalArgumentException("More than one @LyricistStrings(default = true) found"))
                false
            }

            differentTypeCount != 1 -> {
                logger.exception(IllegalArgumentException("All @LyricistStrings must have the same type"))
                false
            }

            else -> true
        }
    }

    private fun KSPropertyDeclaration.getClassSimpleName(): String? =
        getter?.returnType?.resolve()?.declaration?.simpleName?.asString()

    private fun KSPropertyDeclaration.getClassQualifiedName(): String? =
        getter?.returnType?.resolve()?.declaration?.qualifiedName?.asString()

    private fun Sequence<KSAnnotation>.getDefaultLanguageTag(): String? =
        firstOrNull {
            withName(ANNOTATION_NAME)
                ?.arguments
                ?.withName(ANNOTATION_PARAM_DEFAULT)
                ?.value == true
        }?.arguments
            ?.withName(ANNOTATION_PARAM_LANGUAGE_TAG)
            ?.value as? String

    private inline fun <reified T> Sequence<KSAnnotation>.getValue(argumentName: String): T? =
        withName(ANNOTATION_NAME)
            ?.arguments
            ?.withName(argumentName)
            ?.value as? T

    private fun Sequence<KSAnnotation>.withName(name: String): KSAnnotation? =
        firstOrNull { it.shortName.getShortName() == name }

    private fun List<KSValueArgument>.withName(name: String): KSValueArgument? =
        firstOrNull { it.name?.getShortName() == name }

    private companion object {
        val INDENTATION = " ".repeat(4)

        const val ANNOTATION_NAME = "LyricistStrings"
        const val ANNOTATION_PACKAGE = "cafe.adriel.lyricist.$ANNOTATION_NAME"
        const val ANNOTATION_PARAM_LANGUAGE_TAG = "languageTag"
        const val ANNOTATION_PARAM_DEFAULT = "default"
    }
}
