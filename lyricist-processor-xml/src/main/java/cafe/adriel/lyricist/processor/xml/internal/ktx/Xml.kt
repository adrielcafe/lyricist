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

internal fun FileTreeWalk.filterXmlStringFiles(): Sequence<File> =
    filter {
        it.isFile &&
            it.nameWithoutExtension.contains(STRINGS_FILE_NAME, ignoreCase = true) &&
            it.parentFile.name.startsWith(VALUES_FOLDER_NAME, ignoreCase = true)
    }

internal fun File.getXmlStrings(): List<Pair<ResourceName, StringResource>> =
    konsumeXml()
        .child(TAG_RESOURCES) {
            getPlainStrings() + getStringArrays() + getPlurals()
        }

private fun Konsumer.getPlainStrings(): List<Pair<String, StringResource>> =
    children(TAG_STRING) {
        attributes[ATTRIBUTE_NAME] to StringResource.PlainString(
            value = text()
        )
    }

private fun Konsumer.getStringArrays(): List<Pair<String, StringResource>> =
    children(TAG_STRING_ARRAY) {
        attributes[ATTRIBUTE_NAME] to StringResource.StringArray(
            value = children(TAG_ITEM) { text() }
        )
    }

private fun Konsumer.getPlurals(): List<Pair<String, StringResource>> =
    children(TAG_PLURALS) {
        attributes[ATTRIBUTE_NAME] to StringResource.Plurals(
            value = children(TAG_ITEM) {
                Quantity.valueOf(attributes[ATTRIBUTE_QUANTITY].uppercase()) to text()
            }.toMap()
        )
    }
