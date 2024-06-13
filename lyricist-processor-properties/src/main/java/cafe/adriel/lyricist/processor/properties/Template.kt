package cafe.adriel.lyricist.processor.properties

internal fun createTemplate(className: String, fields: String): String =
    """
    |public data class $className(
    |    $fields
    |)
    """.trimMargin()
