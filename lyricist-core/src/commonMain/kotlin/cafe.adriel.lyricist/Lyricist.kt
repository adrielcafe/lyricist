package cafe.adriel.lyricist

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

public typealias LanguageTag = String

public class Lyricist<T>(
    private val defaultLanguageTag: LanguageTag,
    private val layoutDirections: Map<LanguageTag, LayoutDirection>,
    private val translations: Map<LanguageTag, T>
) {

    private val mutableState: MutableStateFlow<LyricistState<T>> =
        MutableStateFlow(
            LyricistState(
                defaultLanguageTag,
                getLayoutDirection(defaultLanguageTag),
                getStrings(defaultLanguageTag)
            )
        )

    public val state: StateFlow<LyricistState<T>> =
        mutableState.asStateFlow()

    public var languageTag: LanguageTag
        get() = mutableState.value.languageTag
        set(languageTag) {
            mutableState.value = LyricistState(
                languageTag,
                getLayoutDirection(languageTag),
                getStrings(languageTag)
            )
        }

    public val layoutDirection: LayoutDirection
        get() = mutableState.value.layoutDirection


    public val strings: T
        get() = mutableState.value.strings

    private val LanguageTag.fallback: LanguageTag
        get() = split(FALLBACK_REGEX).first()

    private fun getStrings(languageTag: LanguageTag) =
        translations[languageTag]
            ?: translations[languageTag.fallback]
            ?: translations[defaultLanguageTag]
            ?: throw LyricistException("Strings for language tag $languageTag not found")

    private fun getLayoutDirection(languageTag: LanguageTag): LayoutDirection =
        layoutDirections[languageTag]
            ?: layoutDirections[languageTag.fallback]
            ?: layoutDirections[defaultLanguageTag]
            ?: throw LyricistException("LayoutDirection for language tag $languageTag not found")

    private companion object {
        private val FALLBACK_REGEX = Regex("[-_]")
    }
}

public data class LyricistState<T> internal constructor(
    val languageTag: LanguageTag,
    val layoutDirection: LayoutDirection,
    val strings: T,
)

public class LyricistException internal constructor(
    override val message: String
) : RuntimeException()

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public annotation class LyricistStrings(
    val languageTag: LanguageTag,
    val layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    val default: Boolean = false
)

public enum class LayoutDirection {
    Ltr, Rtl
}
