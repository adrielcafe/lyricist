[![Android API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![kotlin](https://img.shields.io/github/languages/top/adrielcafe/lyricist.svg?style=for-the-badge)](https://kotlinlang.org/)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?style=for-the-badge)](https://ktlint.github.io/)
[![License MIT](https://img.shields.io/github/license/adrielcafe/lyricist.svg?style=for-the-badge&color=yellow)](https://opensource.org/licenses/MIT)

### **Lyricist** is a lightweight [I18N and I10N](https://en.wikipedia.org/wiki/Internationalization_and_localization) library for Jetpack Compose.

#### Next steps
* Generate the `Map<Locale, Strings>` and `LocalStrings`
* Generate the `Strings` class through existing `strings.xml` files

#### Why _Lyricist_?
Inspired by [accompanist](https://github.com/google/accompanist#why-the-name): music composing is done by a composer, and since this library is about writing ~~lyrics~~ strings, the role of a [lyricist](https://en.wikipedia.org/wiki/Lyricist) felt like a good name.

## Usage
Take a look at the [sample app](https://github.com/adrielcafe/lyricist/tree/main/sample/src/main/java/cafe/adriel/lyricist/sample) for a working example.

First, create a `data class`, `class` or `interface` and declare your strings. The strings can be anything: `Char`, `String`, `AnnotatedString`, `List<String>`, `Set<String>` or even lambdas!
```kotlin
data class Strings(
    val simpleString: String,
    val annotatedString: AnnotatedString,
    val parameterString: (locale: String) -> String,
    val pluralString: (count: Int) -> String,
    val listStrings: List<String>
)
```

Next, create instances for each supported language. I recommend to follow the coding convention for [singleton objects](https://kotlinlang.org/docs/coding-conventions.html#property-names).
```kotlin
val EnStrings = Strings(
    simpleString = "Hello Compose!",

    annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red, fontFamily = FontFamily.Cursive)) { append("Hello ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Light, fontSize = 16.sp)) { append("Compose!") }
    },

    parameterString = { locale ->
        "Current locale: $locale"
    },

    pluralString = { count ->
        val value = when (count) {
            1 -> "$count wish"
            else -> "$count wishes"
        }
        "You have $value remaining"
    },

    listStrings = listOf("Avocado", "Pineapple", "Plum", "Coconut")
)

val PtStrings = Strings(/* pt strings */)

val EsStrings = Strings(/* es strings */)

val RuStrings = Strings(/* ru strings */)
```

Now map all supported languages to the corresponding strings. You should use the [Locale](https://developer.android.com/reference/kotlin/androidx/compose/ui/text/intl/Locale) class for the keys.
```kotlin
val strings = mapOf(
    Locales.EN to EnStrings,
    Locales.PT to PtStrings,
    Locales.ES to EsStrings,
    Locales.RU to RuStrings,
)
```

To finish the setup, create a [CompositionLocal](https://developer.android.com/reference/kotlin/androidx/compose/runtime/CompositionLocal) to access the current strings inside your Composable functions. You can also provide a default value.
```kotlin
val LocalStrings = staticCompositionLocalOf { EnStrings }
```

Finally you can use Lyricist to help you handle locale changes and access the current strings. Simply call `rememberLyricist()` with the `Map<Locale, Strings>` created before. After that, call `ProvideStrings()` to make your `CompositionLocal` accessible down the tree.
```kotlin
val lyricist = rememberLyricist(strings)

ProvideStrings(lyricist, LocalStrings) {
    // Content
}
```

As any other `CompositionLocal`, is pretty simple to retrieve the current strings.
```kotlin
val strings = LocalStrings.current

// Simple simple
Text(text = strings.simpleString)

// Annotated string
Text(text = strings.annotatedString)

// Parameter string
Text(text = strings.parameterString(lyricist.currentLocale.toLanguageTag()))

// Plural string
Text(text = strings.pluralString(2))
Text(text = strings.pluralString(1))

// List string
Text(text = strings.listStrings.joinToString())
```

And also to change the current locale.
```kotlin
lyricist.currentLocale = Locales.PT
```

**Important:** Lyricist won't persist the current locale on storage, is outside its scope. It uses the System locale as default.

## Import to your project
TODO

## Developed by
* [Adriel Café](http://github.com/adrielcafe) | [@adrielcafe](https://twitter.com/adrielcafe)
* [Gabriel Souza](https://github.com/DevSrSouza/) | [@devsrsouza](https://twitter.com/devsrsouza)