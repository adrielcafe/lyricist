package cafe.adriel.lyricist.processor

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public annotation class Strings(
    val languageTag: String,
    val default: Boolean = false
)
