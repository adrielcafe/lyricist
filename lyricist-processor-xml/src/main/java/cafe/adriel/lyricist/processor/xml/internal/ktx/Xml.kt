package cafe.adriel.lyricist.processor.xml.internal.ktx

import cafe.adriel.lyricist.processor.xml.internal.Quantity
import cafe.adriel.lyricist.processor.xml.internal.ResourceName
import cafe.adriel.lyricist.processor.xml.internal.StringResource
import com.gitlab.mvysny.konsumexml.Konsumer
import com.gitlab.mvysny.konsumexml.konsumeXml
import java.io.File

private const val VALUES_FOLDER_NAME = "values"
private const val STRINGS_FILE_NAME = "string"

private const val ATTRIBUTE_NAME = "name"
private const val ATTRIBUTE_QUANTITY = "quantity"

private const val TAG_RESOURCES = "resources"
private const val TAG_STRING = "string"
private const val TAG_STRING_ARRAY = "string-array"
private const val TAG_PLURALS = "plurals"
private const val TAG_ITEM = "item"

private val ReplacementExpr = Regex("@string/(\\S+)")
private var replacements = mutableListOf<String>()

internal fun FileTreeWalk.filterXmlStringFiles(): Sequence<File> =
    filter {
        it.isFile &&
            it.nameWithoutExtension.contains(STRINGS_FILE_NAME, ignoreCase = true) &&
            it.parentFile.name.startsWith(VALUES_FOLDER_NAME, ignoreCase = true)
    }

private typealias ResourceNodes = List<Pair<ResourceName, StringResource>>

internal fun File.getXmlStrings(): ResourceNodes {
    replacements = mutableListOf()
    return konsumeXml()
        .child(TAG_RESOURCES) {
            getPlainStrings() + getStringArrays() + getPlurals()
        }
        .applyReplacements()
}

private fun ResourceNodes.applyReplacements(): ResourceNodes {
    val values = toMutableList()

    // We need to do late replacements because Konsume XML resolves everything on-demand,
    // there's no get("something") so trying to replace value of "A" node in "B" while "B" is
    // being resolved is impossible
    for (replacement in replacements) {
        val index = values.indexOfFirst { (key, _) -> key == replacement }
        val (key, resource) = values[index]
        val replaced: StringResource = when (resource) {
            is StringResource.PlainString -> resource.copy(
                value = replaceResourceValueExpr(resource.value, values)
            )
            is StringResource.Plurals -> resource.copy(
                value = resource.value.mapValues { (_, nodeText) ->
                    replaceResourceValueExpr(nodeText, values)
                }
            )
            is StringResource.StringArray -> resource.copy(
                value = resource.value.map { nodeText -> replaceResourceValueExpr(nodeText, values) }
            )
        }

        values[index] = key to replaced
    }

    return values
}

private fun replaceResourceValueExpr(currentValue: String, values: ResourceNodes) =
    ReplacementExpr.replace(currentValue) { match ->
        val target = match.groupValues.last()
        // Searches for `<string name="...">`
        val (_, resource) = values.first { (name, _) -> name == target }
        (resource as StringResource.PlainString).value
    }

private fun Konsumer.getPlainStrings(): ResourceNodes =
    children(TAG_STRING) {
        val attr = attributes[ATTRIBUTE_NAME]
        attr to StringResource.PlainString(
            value = textWithReplacements(attr)
        )
    }

private fun Konsumer.getStringArrays(): ResourceNodes =
    children(TAG_STRING_ARRAY) {
        val attr = attributes[ATTRIBUTE_NAME]
        attr to StringResource.StringArray(
            value = children(TAG_ITEM) { textWithReplacements(attr) }
        )
    }

private fun Konsumer.getPlurals(): ResourceNodes =
    children(TAG_PLURALS) {
        val attr = attributes[ATTRIBUTE_NAME]
        attr to StringResource.Plurals(
            value = children(TAG_ITEM) {
                Quantity.valueOf(attributes[ATTRIBUTE_QUANTITY].uppercase()) to
                    textWithReplacements(attr)
            }.toMap()
        )
    }

private fun Konsumer.textWithReplacements(attr: String): String = text()
    .also { text ->
        if (ReplacementExpr.containsMatchIn(text)) {
            replacements.add(attr)
        }
    }
