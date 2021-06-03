package cafe.adriel.lyricist.processor.xml.internal

internal typealias StringResources = Map<ResourceName, StringResource>

internal typealias LanguageTag = String

internal typealias ResourceName = String

internal sealed class StringResource {
    data class PlainString(val value: String) : StringResource()
    data class StringArray(val value: List<String>) : StringResource()
    data class Plurals(val value: Map<Quantity, String>) : StringResource()
}

internal enum class Quantity {
    ZERO,
    ONE,
    TWO,
    FEW,
    MANY,
    OTHER
}
