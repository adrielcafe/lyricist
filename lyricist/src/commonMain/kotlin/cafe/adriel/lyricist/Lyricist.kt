package cafe.adriel.lyricist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableMap

public class Lyricist<T>(
    private val defaultLanguageTag: LanguageTag,
    private val translations: ImmutableMap<LanguageTag, T>
) {

    public var languageTag: LanguageTag by mutableStateOf(defaultLanguageTag)

    public val strings: T
        get() = translations[languageTag]
            ?: translations[languageTag.fallback]
            ?: translations[defaultLanguageTag]
            ?: error("Strings for language tag $languageTag not found")

    private val LanguageTag.fallback: LanguageTag
        get() = split(LANGUAGE_TAG_SEPARATOR).first()

    public companion object {
        private const val LANGUAGE_TAG_SEPARATOR = '-'
    }
}
