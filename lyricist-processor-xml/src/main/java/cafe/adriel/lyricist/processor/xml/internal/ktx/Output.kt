package cafe.adriel.lyricist.processor.xml.internal.ktx

import cafe.adriel.lyricist.processor.xml.internal.LanguageTag
import cafe.adriel.lyricist.processor.xml.internal.Quantity
import java.io.File

internal val INDENTATION = " ".repeat(4)

private val REGEX_QUOTES = """(?<!\\)"""".toRegex()
private val REGEX_XML_PARAM = """%\d+\$""".toRegex()
private val REGEX_STRING_PARAM = """%[s|d]""".toRegex()
private val REGEX_VALUES_FILE = """values-?""".toRegex()

private const val LANGUAGE_TAG_SEPARATOR_OLD = "-r"
private const val LANGUAGE_TAG_SEPARATOR_NEW = "_"

internal val File.languageTag: LanguageTag
    get() = parentFile.name
        .replace(REGEX_VALUES_FILE, "")
        .replace(LANGUAGE_TAG_SEPARATOR_OLD, LANGUAGE_TAG_SEPARATOR_NEW)

internal val String.normalized: String
    get() = replace(REGEX_QUOTES, """\\\"""")
        .replace(REGEX_XML_PARAM, """%""")

internal val String.params: List<String>
    get() = REGEX_STRING_PARAM.findAll(normalized, 0)
        .map(MatchResult::value)
        .map { param -> if (param == "%d") "Int" else "String" }
        .toList()

internal val List<String>.formatted: String
    get() = joinToString(",\n") { "${INDENTATION.repeat(2)}\"${it.normalized}\"" }

internal val Map<Quantity, String>.formatted: String
    get() = listOfNotNull(
        get(Quantity.ZERO)?.let { "0 ->" to it },
        get(Quantity.ONE)?.let { "1 ->" to it },
        get(Quantity.TWO)?.let { "2 ->" to it },
        default?.let { "else ->" to it }
    ).joinToString("\n") { (key, value) ->
        "${INDENTATION.repeat(3)}$key \"${value.normalized}\""
    }

private val Map<Quantity, String>.default: String?
    get() = get(Quantity.OTHER)
        ?: get(Quantity.MANY)
        ?: get(Quantity.FEW)
        ?: get(Quantity.TWO)
        ?: get(Quantity.ONE)
        ?: get(Quantity.ZERO)
