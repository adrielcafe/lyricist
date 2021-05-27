package cafe.adriel.lyricist.processor.internal

import cafe.adriel.lyricist.processor.Strings
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
import java.io.OutputStream

internal class LyricistSymbolProcessor(
    private val moduleName: String,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val declarations = mutableListOf<KSPropertyDeclaration>()

    private val visitor = LyricistVisitor(declarations)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(Strings::class.java.name)
            .filter { it is KSPropertyDeclaration && it.validate() }
            .forEach { it.accept(visitor, Unit) }

        return emptyList()
    }

    override fun finish() {
        if (validate().not()) return

        val fileName = "${moduleName.toUpperCamelCase()}Strings"

        val defaultStrings = declarations
            .first { it.annotations.getValue<Strings, Boolean>(Strings::default.name) == true }

        val packagesOutput = declarations
            .mapNotNull { it.qualifiedName?.asString() }
            .plus(defaultStrings.getClassQualifiedName())
            .joinToString(separator = "\n") { packageName -> "import $packageName" }

        val stringsClassOutput = defaultStrings.getClassSimpleName()

        val defaultStringsOutput = defaultStrings.simpleName.getShortName()

        val translationMappingOutput = declarations
            .map {
                it.annotations.getValue<Strings, String>(Strings::languageTag.name)!! to it.simpleName.getShortName()
            }.joinToString(",\n") { (languageTag, property) ->
                "$INDENTATION\"$languageTag\" to $property"
            }

        codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = true,
                sources = declarations.map { it.containingFile!! }.toTypedArray()
            ),
            packageName = PACKAGE_NAME,
            fileName = fileName
        ).use { stream ->
            stream + """
                |package $PACKAGE_NAME
                |
                |import androidx.compose.runtime.Composable
                |import androidx.compose.runtime.CompositionLocalProvider
                |import androidx.compose.runtime.remember
                |import androidx.compose.runtime.staticCompositionLocalOf
                |import androidx.compose.ui.text.intl.Locale
                |$packagesOutput
                |
                |private val strings = mapOf(
                |$translationMappingOutput
                |)
                |
                |val Local$fileName = staticCompositionLocalOf { $defaultStringsOutput }
                |
                |@Composable
                |public fun remember$fileName(
                |    languageTag: LanguageTag = Locale.current.toLanguageTag()
                |): Lyricist<$stringsClassOutput> =
                |    rememberStrings(strings, languageTag)
                |
                |@Composable
                |public fun Provide$fileName(
                |    lyricist: Lyricist<$stringsClassOutput>,
                |    content: @Composable () -> Unit
                |) {
                |    ProvideStrings(lyricist, Local$fileName, content)
                |}
                """.trimMargin()
        }
    }

    private fun validate(): Boolean {
        val defaultCount = declarations
            .count { it.annotations.getValue<Strings, Boolean>(Strings::default.name) == true }

        val differentTypeCount = declarations
            .groupBy { it.getClassQualifiedName() }
            .count()

        return when {
            defaultCount == 0 -> {
                logger.exception(IllegalArgumentException("No @Strings(default = true) found"))
                false
            }
            defaultCount > 1 -> {
                logger.exception(IllegalArgumentException("More than one @Strings(default = true) found"))
                false
            }
            differentTypeCount != 1 -> {
                logger.exception(IllegalArgumentException("All @Strings must have the same type"))
                false
            }
            else -> true
        }
    }

    private fun KSPropertyDeclaration.getClassSimpleName(): String? =
        getter?.returnType?.resolve()?.declaration?.simpleName?.asString()

    private fun KSPropertyDeclaration.getClassQualifiedName(): String? =
        getter?.returnType?.resolve()?.declaration?.qualifiedName?.asString()

    private inline fun <reified A : Annotation, reified T> List<KSAnnotation>.getValue(argumentName: String): T? =
        withName(A::class.simpleName!!)
            ?.arguments?.withName(argumentName)
            ?.value as? T

    private fun List<KSAnnotation>.withName(name: String): KSAnnotation? =
        firstOrNull { it.shortName.getShortName() == name }

    private fun List<KSValueArgument>.withName(name: String): KSValueArgument? =
        firstOrNull { it.name?.getShortName() == name }

    private operator fun OutputStream.plus(line: String) {
        write("$line\n".toByteArray())
    }

    private companion object {
        const val PACKAGE_NAME = "cafe.adriel.lyricist"
        const val INDENTATION = "    "
    }
}
