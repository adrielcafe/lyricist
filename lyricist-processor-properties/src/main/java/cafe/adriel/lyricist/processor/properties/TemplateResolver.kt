package cafe.adriel.lyricist.processor.properties

import java.util.Properties

public fun resolveProperties(properties: Properties): String = properties.map { (key, value) ->
    val name = removeDots(key as String)
    val (type, fieldValue) = resolveType(value as String)

    "val $name: $type = $fieldValue"
}.joinToString(separator = ",\n")

private fun removeDots(input: String): String {
    var replaced = input
    while (true) {
        val dot = replaced.indexOf(".")
        if (dot == -1) break

        val first = replaced[dot + 1]

        replaced = replaced.replaceRange(dot..dot + 1, first.uppercase())
    }
    return replaced
}

private fun resolveType(value: String): Pair<String, String> = when {
    value.toBooleanStrictOrNull() != null -> "Boolean" to value
    value.toIntOrNull() != null -> "Int" to value
    else -> resolveComplexExpressionType(value) ?: ("String" to "\"$value\"")
}

private val ValueWithArgumentsExpr = Regex("\\{(.*?)}")

private fun resolveComplexExpressionType(value: String): Pair<String, String>? {
    val matches = ValueWithArgumentsExpr.findAll(value)
    val textArgs = matches.map(MatchResult::value).toList()
    if (textArgs.isEmpty())
        return null

    val names = textArgs.map { name -> name.substring(1 until name.lastIndex) }
    val fnArgs = names.joinToString(separator = ", ") { name -> "$name: String" }
    val returnArgs = names.joinToString(separator = ", ")
    var replacedValue = value
    for (match in matches)
        replacedValue = replacedValue.replace(match.value, "$${match.value}")

    return "($fnArgs) -> String" to "{ $returnArgs -> \"$replacedValue\" }"
}
