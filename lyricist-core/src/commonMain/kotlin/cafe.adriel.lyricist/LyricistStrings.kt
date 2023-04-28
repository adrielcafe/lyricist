package cafe.adriel.lyricist

public typealias LanguageTag = String

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public annotation class LyricistStrings(
    val languageTag: LanguageTag,
    val default: Boolean = false
)
